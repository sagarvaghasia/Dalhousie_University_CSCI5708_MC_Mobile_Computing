package com.example.group7project.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.group7project.R
import com.example.group7project.`interface`.Communicator
import com.example.group7project.fragments.*
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.Meeting

class ScheduleMeetingActivity : AppCompatActivity(), Communicator {

    private var secondaryUserEmail: String? = String();
    private var chatId: String? = String();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meeting)

        var bundle = intent.extras;
        if (bundle != null) {
            secondaryUserEmail = bundle.getString("secondaryUserEmail")
            chatId = bundle.getString("chatId");
        }
    }

    override fun onStart() {
        super.onStart()

        var bundle: Bundle = Bundle();
        bundle.putString("secondaryUserEmail", secondaryUserEmail)

        var meetingListFragment: ScheduledMeetingListFragment = ScheduledMeetingListFragment()
        meetingListFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.scheduleFragmentContainerView, meetingListFragment).commit();
    }

    override fun goToScheduleMeetingFragment() {
        var bundle: Bundle = Bundle();
        bundle.putString("secondaryUserEmail", secondaryUserEmail)
        bundle.putString("chatId", chatId)
        var meetingFragment: MeetingFragment = MeetingFragment()
        meetingFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.scheduleFragmentContainerView, meetingFragment).commit();
    }

    override fun goBackFun() {
        var bundle: Bundle = Bundle();
        bundle.putString("secondaryUserEmail", secondaryUserEmail)
        var meetingFragment: ScheduledMeetingListFragment = ScheduledMeetingListFragment()
        meetingFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.scheduleFragmentContainerView, meetingFragment).commit();
    }

    override fun goToConfirmScheduleMeetingFragment(meetingModel: Meeting) {

        if (!meetingModel.isMeetingAccepted) // that means meeting has been scheduled, but not accepted till now
        {
            if (meetingModel.scheduledBy.equals(CurrentUser.userType)) // checks whether the meeting scheduled by the person is same as the current user
            {
                // navigating to view scheduled meeting page
                var bundle: Bundle = Bundle();
                bundle.putString("scheduledDate", meetingModel.scheduledDate);
                bundle.putString("scheduledTime", meetingModel.scheduledTime);
                bundle.putString("title", meetingModel.title);

                var status = String();

                if (meetingModel.isMeetingScheduled) {
                    //checks if meeting has been accepted
                    if (meetingModel.isMeetingAccepted) {
                        status = "Accepted"
                    } else if (meetingModel.isMeetingRejected) { // checks if meeting is rejected
                        status = "Decline"
                    } else {
                        status = "Pending"
                    }
                }

                bundle.putString("status", status);
                var viewMeetingFragment: ViewMeetingFragment = ViewMeetingFragment();
                viewMeetingFragment.arguments = bundle;
                setFragment(viewMeetingFragment);
            } else {
                // navigating to confirm meeting
                var bundle: Bundle = Bundle();
                bundle.putString("matchedUserId", secondaryUserEmail);
                bundle.putString("scheduledDate", meetingModel.scheduledDate);
                bundle.putString("scheduledTime", meetingModel.scheduledTime);
                bundle.putString("meetingId", meetingModel.meetingId);
                bundle.putString("chatId", chatId)

                var confirmMeetingFragment: ConfirmMeetingFragment = ConfirmMeetingFragment();
                confirmMeetingFragment.arguments = bundle;
                setFragment(confirmMeetingFragment);
            }
        } else // meeting has been scheduled and accepted
        {
            // navigating to schedule meeting accepted page
            var bundle: Bundle = Bundle();
            bundle.putString("scheduledDate", meetingModel.scheduledDate);
            bundle.putString("scheduledTime", meetingModel.scheduledTime);
            bundle.putString("title", meetingModel.title);

            var status = String();
            if (meetingModel.isMeetingAccepted) {
                status = "Accepted"
            } else if (meetingModel.isMeetingScheduled) {
                status = "Pending"
            } else {
                status = "Decline"
            }

            bundle.putString("status", status);
            var viewMeetingFragment: ViewMeetingFragment = ViewMeetingFragment();
            viewMeetingFragment.arguments = bundle;
            setFragment(viewMeetingFragment);
        }
    }

    // this function replaces the meeting container view with the received fragment
    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.scheduleFragmentContainerView, fragment).commit();
    }
}