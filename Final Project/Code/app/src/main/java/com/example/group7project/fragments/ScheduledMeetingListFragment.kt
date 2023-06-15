package com.example.group7project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.group7project.*
import com.example.group7project.`interface`.Communicator
import com.example.group7project.adapters.MeetingAdapter
import com.example.group7project.model.Meeting
import com.google.android.material.floatingactionbutton.FloatingActionButton

//  Reference: https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
class ScheduledMeetingListFragment : Fragment() {

    private var matchUserId: String? = null;

    private lateinit var comm: Communicator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            matchUserId = it.getString("secondaryUserEmail")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_scheduled_meeting_list, container, false)

        val recyclerview = view.findViewById<RecyclerView>(R.id.meetingRecyclerView)
        recyclerview.layoutManager = LinearLayoutManager(context)

        val meeting = Meeting()
        var meetingList = mutableListOf<Meeting>()

        meeting.fetchMeetingList(matchUserId!!) { res ->
            run {

                meetingList = res
                meetingList.reverse()

                //sending values to meeting adapter
                val meetingAdapter = MeetingAdapter(meetingList)

                recyclerview.adapter = meetingAdapter
            }
        };

        addMeetingClickListener(view)

        return view
    }

    private fun addMeetingClickListener(view: View) {
        comm = activity as Communicator
        var btn: FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.addMeeting);
        // adding listener to schedule meeting button
        btn.setOnClickListener {
            comm.goToScheduleMeetingFragment()
        }
    }
}