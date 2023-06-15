package com.example.group7project.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.example.group7project.R
import com.example.group7project.`interface`.Communicator
import com.example.group7project.model.Chat
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.Meeting
import com.google.android.material.button.MaterialButton

class MeetingFragment : Fragment() {


    private var matchUserId: String? = null;
    private var chatId: String? = null;
    private lateinit var meeting: Meeting;
    private lateinit var date: DatePicker
    private lateinit var time: TimePicker

    private lateinit var title: TextView

    private lateinit var scheduleBtn: MaterialButton

    private lateinit var comm: Communicator

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_schedule_meeting, container, false)

        date = view.findViewById<DatePicker>(R.id.scheduleDate)

        time = view.findViewById<TimePicker>(R.id.scheduleTime)

        title = view.findViewById<TextView>(R.id.scheduleMeetingTitle)

        arguments?.let {
            matchUserId = it.getString("secondaryUserEmail")
            chatId = it.getString("chatId")
        }

        meeting = Meeting();
        meeting.setDetails(matchUserId!!) { res ->
            run {
            }
        };
        scheduleBtn = view.findViewById<MaterialButton>(R.id.btn_schedule)

        // adding listener to schedule meeting button
        scheduleBtn.setOnClickListener {
            scheduleMeeting()
        }
        return view
    }

    @SuppressLint("NewApi")
    private fun scheduleMeeting() {
        //storing date as a string
        val scheduledDate = "${date.dayOfMonth}/${date.month}/${date.year}"
        var timeConvention = "AM"
        if (time.hour > 12) {
            timeConvention = "PM"
        }

        //storing time as a string
        val scheduledTime = "${time.hour}:${time.minute} $timeConvention"

        meeting.scheduledBy = CurrentUser.userType
        meeting.scheduledDate = scheduledDate;
        meeting.scheduledTime = scheduledTime
        meeting.title = title.text.toString()

        comm = activity as Communicator

        meeting.createMeeting {
            // once the meeting has been scheduled , add scheduled meeting message to system message
            var chat: Chat = Chat();
            var message: String = String();
            message = "Meeting Scheduled.";
            // storing a meeting scheduled message as system message.
            chat.sendMessage(chatId, "system", message) {}
            activity?.onBackPressed();
        }
        comm.goBackFun()
    }

}