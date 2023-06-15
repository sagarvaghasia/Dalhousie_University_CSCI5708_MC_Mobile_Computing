package com.example.group7project.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.group7project.R
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.group7project.MainActivity
import com.example.group7project.model.Message
import com.example.group7project.adapters.MessagesAdapter
import com.example.group7project.model.Chat
import com.example.group7project.model.CurrentUser
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.util.Calendar


// this activity is responsible for chat between the logged in user and one of the matched users.
class ChatActivity : AppCompatActivity() {

    // declaring the required variables
    private var userId: String? = String();
    private var matchUserId: String? = String();
    private var chatId: String? = String();
    private var image: String? = null;
    private var chatUserName: String? = null;
    private lateinit var chat: Chat;

    private lateinit var messagesAdapter: MessagesAdapter;
    private lateinit var recylerView: RecyclerView;
    private lateinit var progressIndicator: CircularProgressIndicator;
    private lateinit var matchDisplayPicture: ImageView;
    private lateinit var chatUserNameTextView: TextView;

    private lateinit var dbReference: DatabaseReference
    private lateinit var messageList: MutableList<Message>
    private val usersDbRef = FirebaseFirestore.getInstance().collection("users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        progressIndicator = findViewById<CircularProgressIndicator>(R.id.chatLoadProgressIndicator);
        progressIndicator.visibility = View.VISIBLE;
        matchDisplayPicture = findViewById<ImageView>(R.id.chatUserDisplayImage);
        chatUserNameTextView = findViewById<TextView>(R.id.chatUserNameTextView);

        var extrasParameters = intent.extras;
        // getting the parameters received in extras from where this activity was launched
        if (extrasParameters != null) {
            //setting the parameters received from the caller
            userId = extrasParameters.getString("userId");
            matchUserId = extrasParameters.getString("matchUserId");
            chatId = extrasParameters.getString("chatId");
            image = extrasParameters.getString("image");
            chatUserName = extrasParameters.getString("chatUserName");

            // if we don't receive any of the userId, matchUserId or chatId.. finish the activity.
            if (userId.isNullOrEmpty() || matchUserId.isNullOrEmpty() || chatId.isNullOrEmpty()) {
                finish();
            }

            this.chat = Chat(CurrentUser.email, matchUserId.toString(), CurrentUser.userType);
            Glide.with(this).load(image).into(matchDisplayPicture);
            chatUserNameTextView.setText(chatUserName);
        }


        val chatMenuActionButton: ImageButton =
            findViewById<ImageButton>(R.id.chatMenuActionButton);
        val popupMenu: PopupMenu = PopupMenu(this, chatMenuActionButton);
        popupMenu.menuInflater.inflate(R.menu.chat_menu, popupMenu.menu);

        // setting on menu click listener for unmatch , deal and schedule
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.unmatch -> {
                    onUnmatch()
                    Toast.makeText(
                        this@ChatActivity,
                        "Successfully unmatched",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.deal -> {
                    val i = Intent(this, DealActivity::class.java)
                    i.putExtra("secondaryUserEmail", matchUserId);
                    i.putExtra("chatId", chatId);
                    startActivity(i);
                }
                R.id.meet -> {
                    val i = Intent(this, ScheduleMeetingActivity::class.java)
                    i.putExtra("secondaryUserEmail", matchUserId);
                    i.putExtra("chatId", chatId);
                    startActivity(i);
                }
            }
            true
        })

        chatMenuActionButton.setOnClickListener {
            popupMenu.show();
        }

        messageList = mutableListOf<Message>()

        fetchMessageList()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs);
    }

    private fun fetchMessageList() {
        // Reference: https://codingzest.com/firebase-realtime-database-crud-operations-android-kotlin/
        dbReference = Firebase.database.getReference("chat")
        var chatRef = dbReference.child(chatId.toString()).child("messages")

        // adding listener so that any change it real time DB can be reflected immediately
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                if (snapshot.exists()) {
                    for (chatSnap in snapshot.children) {
                        val messageData = chatSnap.getValue(Message::class.java)
                        if (messageData != null) {
                            messageList.add(messageData)
                        }
                    }
                    // sending the updated chat list to the message adapter
                    messagesAdapter =
                        MessagesAdapter(messageList, userId.toString(), matchUserId.toString());
                    progressIndicator.visibility = View.GONE;
                    //setting up the recycler view for the home fragment
                    recylerView = findViewById<View>(R.id.matchesRecyclerView1) as RecyclerView
                    recylerView.apply {
                        setHasFixedSize(false)
                        layoutManager = LinearLayoutManager(context)
                        adapter = messagesAdapter
                        scrollToPosition(messageList.size - 1)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("Fetching Chat Messages:", "Failure")
            }
        })
    }

    fun onMessageSend(v: View) {
        // Reference: https://medium.com/firebase-tips-tricks/how-to-read-data-from-firebase-realtime-database-using-get-269ef3e179c5
        val msgText = findViewById<View>(R.id.messageText) as TextView
        val message = Message(
            userId.toString(),
            msgText.text.toString(),
            Calendar.getInstance().time.toString()
        )

        messageList.add(message)

        dbReference.child(chatId.toString()).child("messages").setValue(messageList)
            .addOnCompleteListener {
                msgText.text = ""
            }.addOnFailureListener {
                Log.i("Message Send", "Failed while storing it to DB")
            }
    }

    fun onUnmatch() {
        var secondaryUserMatches: ArrayList<String> = ArrayList()
        var secondaryUserLeftSwipes: ArrayList<String> = ArrayList()

        val query: Query = usersDbRef.whereEqualTo("email", matchUserId)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (documentSnapshot in task.result!!) {
                    if (documentSnapshot != null) {
                        val element = documentSnapshot.data
                        secondaryUserLeftSwipes =
                            if (element["leftSwipes"] != null) element["leftSwipes"] as ArrayList<String> else ArrayList()
                        val secondaryUserMatches =
                            if (element["matches"] != null) element["matches"] as ArrayList<String> else ArrayList()

                        secondaryUserMatches.remove(userId.toString())
                        secondaryUserLeftSwipes.remove(userId.toString())
                        secondaryUserLeftSwipes.add(userId.toString())

                        usersDbRef.document(matchUserId.toString())
                            .update("matches", secondaryUserMatches)
                        usersDbRef.document(matchUserId.toString())
                            .update("leftSwipes", secondaryUserLeftSwipes)

                        CurrentUser.matches.remove(matchUserId.toString())
                        CurrentUser.leftSwipes.remove(matchUserId.toString())
                        CurrentUser.leftSwipes.add(matchUserId.toString())

                        usersDbRef.document(userId.toString())
                            .update("matches", CurrentUser.matches)
                        usersDbRef.document(userId.toString())
                            .update("leftSwipes", CurrentUser.leftSwipes)

                        println("$userId unmatched with $matchUserId")
                    }
                }
            }
        }

        val intent = Intent(this, HomeActivity::class.java)
        Log.i("infotag", "inside redirect to main activity")
        startActivity(intent)
    }

}