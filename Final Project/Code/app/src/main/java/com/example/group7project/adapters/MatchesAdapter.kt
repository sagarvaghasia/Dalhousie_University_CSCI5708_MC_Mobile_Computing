package com.example.group7project.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.group7project.R
import com.example.group7project.activities.ChatActivity
import com.example.group7project.model.Chat
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.Match
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator

// adapter class for recyler view
// referred from official documenttation
// source -https://developer.android.com/develop/ui/views/layout/recyclerview?gclid=Cj0KCQjwkt6aBhDKARIsAAyeLJ3neHJ19-12JVQ0NMYxO3GSLbf5ej_WKA6W29YwD1CPiHxP21bS0QMaAv8eEALw_wcB&gclsrc=aw.ds

class MatchesAdapter(private var matchesList: ArrayList<Match>, var fragment: Fragment) :
    RecyclerView.Adapter<MatchesAdapter.MatchesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false);
        return MatchesViewHolder(view, this.fragment);
    }

    // It relaces the content of the view with the actual data
    override fun onBindViewHolder(matchesViewHolder: MatchesViewHolder, position: Int) {
        val currentMatch = matchesList[position];
        matchesViewHolder.data = currentMatch;
        matchesViewHolder.matchNameText.setText(currentMatch.name);
        Glide.with(fragment).load(currentMatch.imageURL)
            .into(matchesViewHolder.matchDisplayPicture);
        if (currentMatch.isLastMessageByMatchedUser!!) // sent by the matched user
        {
            matchesViewHolder.lastMessageText.setText(currentMatch.lastMessage);
        } else // not sent by matched User
        {
            matchesViewHolder.lastMessageText.setText("You: " + currentMatch.lastMessage);
        }
    }

    // Return the size of total matches
    override fun getItemCount(): Int {
        return matchesList.size;
    }

    // this matches view holder class provides reference to type of views
    class MatchesViewHolder(private val view: View, private var fragment: Fragment) :
        RecyclerView.ViewHolder(view) {
        var matchNameText: TextView;
        var lastMessageText: TextView;
        var matchDisplayPicture: ImageView;
        lateinit var data: Match;

        init {
            matchNameText = view.findViewById<TextView>(R.id.matchNameTextView);
            lastMessageText = view.findViewById<TextView>(R.id.lastMessageTextView);
            matchDisplayPicture = view.findViewById<ImageView>(R.id.matchDisplayPictureId);
            var layout = view.findViewById<View>(R.id.matchItemLayout);

            // setting on click listener on clicking any of the match
            layout.setOnClickListener {
                this.fragment.view?.findViewById<CircularProgressIndicator>(R.id.progressIndicator)?.visibility =
                    View.VISIBLE;
                var chat = Chat(CurrentUser.email, data.email.toString(), CurrentUser.userType);
                chat.setDetails { res ->
                    run {
                        // navigating to the chat activity which will show the conversation
                        val intent = Intent(view.context, ChatActivity::class.java);
                        intent.putExtra("chatId", chat.chatId);
                        intent.putExtra("userId", CurrentUser.email);
                        intent.putExtra("matchUserId", data.email);
                        intent.putExtra("image", data.imageURL);
                        intent.putExtra("chatUserName", data.name);
                        // removing the visibility of the progress spinner
                        this.fragment.view?.findViewById<CircularProgressIndicator>(R.id.progressIndicator)?.visibility =
                            View.INVISIBLE;
                        view.context.startActivity(intent); // starting chat activity for the selected match
                    }
                }
            }
        }
    }

}