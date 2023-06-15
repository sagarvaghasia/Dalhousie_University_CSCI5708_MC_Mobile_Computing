package com.example.group7project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.group7project.R
import com.example.group7project.model.Message

// Reference: https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
class MessagesAdapter(
    private var messages: MutableList<Message>,
    val currentUserId: String,
    val matchedUserId: String
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    companion object {
        val MESSAGE_FROM_CURRENT_USER = 1
        val MESSAGE_FROM_OTHER_USER = 2
        val MESSAGE_FROM_SYSTEM = 3
    }

    class MessageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(message: Message) {
            view.findViewById<TextView>(R.id.messageTV).text = message.message
        }
    }

    // This method is used to know whether the message came from current user or other user
    override fun getItemViewType(position: Int): Int {
        //TODO remove this static User id = 1
        if (messages[position].sentBy.equals(currentUserId)) {
            return MESSAGE_FROM_CURRENT_USER;
        } else if (messages[position].sentBy.equals(matchedUserId)) {
            return MESSAGE_FROM_OTHER_USER;
        } else {
            return MESSAGE_FROM_SYSTEM;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // checks the user type and set the design of the chat message accordingly
        if (viewType == MESSAGE_FROM_CURRENT_USER) {
            return MessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.current_user_message, parent, false)
            )
        } else if (viewType == MESSAGE_FROM_OTHER_USER) {
            return MessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.other_user_message, parent, false)
            )
        } else {
            return MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.system_message, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindData(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}