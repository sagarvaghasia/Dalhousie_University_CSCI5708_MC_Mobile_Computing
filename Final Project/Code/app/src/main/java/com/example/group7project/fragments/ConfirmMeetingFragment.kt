package com.example.group7project.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.group7project.R
import com.example.group7project.`interface`.Communicator
import com.example.group7project.model.Chat
import com.example.group7project.model.Meeting
import com.google.android.material.button.MaterialButton


class ConfirmMeetingFragment : Fragment() {
    private var matchedUserId: String = String();
    private var chatId: String = String();
    private lateinit var scheduledDate: TextView;
    private lateinit var scheduledTime: TextView;
    private lateinit var meeting: Meeting;

    private lateinit var comm: Communicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_confirm_meeting, container, false)

        matchedUserId = requireArguments().getString("matchedUserId").toString();
        chatId = requireArguments().getString("chatId").toString()

        scheduledDate = view.findViewById(R.id.dateValueTextView);
        scheduledTime = view.findViewById(R.id.timeValueTextView);

        meeting = Meeting();

        meeting.meetingId = requireArguments().getString("meetingId").toString();

        scheduledDate.setText(requireArguments().getString("scheduledDate").toString())
        scheduledTime.setText(requireArguments().getString("scheduledTime").toString());

        acceptButtonListener(view)
        rejectButtonListener(view)

        return view
    }

    private fun rejectButtonListener(view: View) {
        var btn: MaterialButton = view.findViewById<MaterialButton>(R.id.btn_rejectMeeting);
        comm = activity as Communicator

        // adding listener to reject meeting button
        btn.setOnClickListener {
            meeting.declineMeeting { res ->
                run {
                    var chat: Chat = Chat();
                    var message: String = String();
                    message = "Meeting Declined.";
                    // storing a meeting rejected message as system message.
                    chat.sendMessage(chatId, "system", message) {}
                    comm.goBackFun()
                }
            }
        }
    }

    private fun acceptButtonListener(view: View) {
        var btn: MaterialButton = view.findViewById<MaterialButton>(R.id.btn_acceptMeeting);
        comm = activity as Communicator

        // adding listener to accept meeting button
        btn.setOnClickListener {
            meeting.acceptMeeting { res ->
                run {
                    var chat: Chat = Chat();
                    var message: String = String();
                    message = "Meeting Accepted.";
                    // storing a meeting accepted message as system message.
                    chat.sendMessage(chatId, "system", message) {}
                    comm.goBackFun()
                }
            }
        }
    }
}
