package com.example.group7project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.group7project.R
import com.example.group7project.`interface`.Communicator
import com.example.group7project.activities.ScheduleMeetingActivity
import com.example.group7project.model.Meeting

// Reference: https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
class MeetingAdapter(private val meetingList: MutableList<Meeting>) :
    RecyclerView.Adapter<MeetingAdapter.ViewHolder>() {

    private lateinit var comm: Communicator
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meeting_recycler_view_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val meetingModel = meetingList[position]

        holder.meetingTitle.text = meetingModel.title
        holder.meetingDate.text = meetingModel.scheduledDate
        holder.meetingTime.text = meetingModel.scheduledTime

        //set the status value based on the values set by the user
        if (meetingModel.isMeetingScheduled) {
            if (meetingModel.isMeetingAccepted) {
                holder.meetingStatus.text = "Accepted"
            } else if (meetingModel.isMeetingRejected) {
                holder.meetingStatus.text = "Decline"
            } else {
                holder.meetingStatus.text = "Pending"
            }
        }

        // adding the listener to meeting details card
        holder.meetingCard.setOnClickListener(View.OnClickListener {

            val activity = it.context as ScheduleMeetingActivity
            comm = activity as Communicator

            // this will open the confirmation page of the meeting
            comm.goToConfirmScheduleMeetingFragment(meetingModel)
        })
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return meetingList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val meetingTitle: TextView = itemView.findViewById(R.id.meetingTitleValue)
        val meetingDate: TextView = itemView.findViewById(R.id.meetingDateValue)
        val meetingTime: TextView = itemView.findViewById(R.id.meetingTimeValue)
        val meetingStatus: TextView = itemView.findViewById(R.id.meetingStatusValue)
        val meetingCard: CardView = itemView.findViewById(R.id.meetingCard)
    }
}