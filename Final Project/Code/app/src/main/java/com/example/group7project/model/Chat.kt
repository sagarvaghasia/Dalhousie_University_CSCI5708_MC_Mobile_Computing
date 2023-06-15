package com.example.group7project.model

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class Chat {
    var chatId: String = String();
    var investor: String = String();
    var startup: String = String();
    var currentUserType: String = String();
    var currentUser: String = String();
    lateinit var dbReference: DatabaseReference;
    var messages: MutableList<Message>? = mutableListOf<Message>()

    constructor() {}

    constructor(currentUser: String, matchedUser: String, currentUserType: String) {
        this.currentUser = currentUser;
        this.currentUserType = currentUserType;

        if (currentUserType.equals("investor"))
            this.investor = currentUser;
        else
            this.startup = currentUser;

        if (currentUserType.equals("investor"))
            this.startup = matchedUser;
        else
            this.investor = matchedUser;
    }

    fun setDetails(callback: (Boolean) -> Unit) {
        setChatIdFromFirestoreDb { res ->
            run { callback(true) }
        }
    }

    // sets the chatId for the chat between investor and the startup
    fun setChatIdFromFirestoreDb(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore;
        val chatsRef = db.collection("chat");
        chatsRef.whereEqualTo("investor", investor).whereEqualTo("startup", startup).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    chatId = document.id; // setting the chat id;
                    callback(true);
                }
                if (result.size() == 0) {
                    var chat = hashMapOf(
                        "investor" to investor,
                        "startup" to startup
                    );
                    db.collection("chat").add(chat)
                        .addOnSuccessListener { documentReference ->
                            this.chatId = documentReference.id;
                            setChatInRealtimeDatabase { res ->
                                run {
                                    callback(true);
                                }
                            }
                            Log.d(
                                "SUCCESS",
                                "Chat DocumentSnapshot written with ID: ${documentReference.id}"
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR", "Error getting documents for chats: ", exception);
                callback(false);
            }
    }

    // Reference: https://codingzest.com/firebase-realtime-database-crud-operations-android-kotlin/
    // Creates a chat in realtime database between the investor and startup
    fun setChatInRealtimeDatabase(callback: (Boolean) -> Unit) {
        var defaultMessageList = mutableListOf<Message>();
        var firstMessage = "Congratulations on getting matched with each other.";
        defaultMessageList.add(
            Message(
                "system",
                firstMessage,
                Calendar.getInstance().time.toString()
            )
        );
        var dbReference = Firebase.database.getReference("chat");
        var chat = hashMapOf(
            "investor" to investor,
            "startup" to startup
        );
        dbReference.child(chatId).setValue(chat)
            .addOnCompleteListener {
                dbReference.child(chatId).child("messages").setValue(defaultMessageList);
                callback(true);
            }.addOnFailureListener {
                callback(false);
            }
    }

    // Reference: https://codingzest.com/firebase-realtime-database-crud-operations-android-kotlin/
    // sends the message to the realtime database for the chatId
    fun sendMessage(chatId: String?, sentBy: String, message: String, callback: (Boolean) -> Unit) {
        dbReference = Firebase.database.getReference("chat")
        var chatRef = dbReference.child(chatId.toString()).child("messages");


        var newMessage = Message(sentBy, message, Calendar.getInstance().time.toString());
        var messageList = mutableListOf<Message>()

        chatRef.get().addOnSuccessListener { snapshot ->
            run {
                for (chatSnap in snapshot.children) {
                    val messageData = chatSnap.getValue(Message::class.java)
                    if (messageData != null) {
                        messageList.add(messageData)
                    }
                }
                messageList.add(newMessage);
                chatRef.setValue(messageList)
                    .addOnCompleteListener {
                        callback(true);
                    }.addOnFailureListener {
                        callback(false);
                    }
            }
        }.addOnFailureListener {
            callback(false);
        }
    }

}