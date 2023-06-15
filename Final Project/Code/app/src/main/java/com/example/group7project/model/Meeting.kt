package com.example.group7project.model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldValue.*
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class Meeting {

    var meetingId: String = String();
    var currentUser: String = String();
    var investor: String = String();
    var startup: String = String();
    var title: String = String();
    var scheduledDate: String = String()
    var scheduledTime: String = String()
    var isMeetingScheduled: Boolean = false
    var isMeetingAccepted: Boolean = false
    var isMeetingRejected: Boolean = false
    var scheduledBy: String = String()
    lateinit var createdAt: FieldValue

    constructor() {
        val user = Firebase.auth.currentUser;
        if (user != null) {
            this.currentUser = user.email.toString();
        }
    }

    // set investor and startup values based on current user type
    fun setDetails(secondaryUserEmail: String, callback: (Boolean) -> Unit) {
        if (CurrentUser.userType.equals("investor")) {
            this.investor = CurrentUser.email;
            this.startup = secondaryUserEmail;
        } else {
            this.investor = secondaryUserEmail;
            this.startup = CurrentUser.email;
        }
    }

    // Used to fetch the meeting list from DB
    fun fetchMeetingList(secondaryUserEmail: String, callback: (MutableList<Meeting>) -> Unit) {
        if (CurrentUser.userType.equals("investor")) {
            this.investor = CurrentUser.email;
            this.startup = secondaryUserEmail;
        } else {
            this.investor = secondaryUserEmail;
            this.startup = CurrentUser.email;
        }

        val db = Firebase.firestore;

        val meetingRef = db.collection("meeting");

        meetingRef.whereEqualTo("investor", investor).whereEqualTo("startup", startup)
            .orderBy("createdAt", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { result ->

                var meetingList = mutableListOf<Meeting>()
                for (document in result) {
                    var meeting = Meeting()

                    // storing the meeting details from DB to meetingList Object
                    meeting.meetingId = document.id
                    meeting.investor = document.data?.get("investor") as String;
                    meeting.startup = document.data?.get("startup") as String;
                    meeting.title = document.data?.get("title") as String;
                    meeting.investor = document.data?.get("investor") as String;
                    meeting.scheduledDate = document.data?.get("scheduledDate") as String;
                    meeting.scheduledTime = document.data?.get("scheduledTime") as String;
                    meeting.isMeetingScheduled =
                        document.data?.get("isMeetingScheduled") as Boolean;
                    meeting.isMeetingAccepted = document.data?.get("isMeetingAccepted") as Boolean;
                    meeting.isMeetingRejected = document.data?.get("isMeetingRejected") as Boolean;
                    meeting.scheduledBy = document.data?.get("scheduledBy") as String;

                    meetingList.add(meeting)
                }
                callback(meetingList)

            }
            .addOnFailureListener { exception ->
                Log.d("ERROR", "Error in fetching documents in meeting: ", exception)
            }
    }

    // used to create the meeting
    fun createMeeting(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore;
        isMeetingScheduled = true
        isMeetingAccepted = false
        isMeetingRejected = false

        createdAt = serverTimestamp()

        val meetingRef = db.collection("meeting");

        val meetingObj = hashMapOf<String, Any>(
            "investor" to investor,
            "startup" to startup,
            "scheduledTime" to scheduledTime,
            "scheduledDate" to scheduledDate,
            "scheduledBy" to scheduledBy,
            "isMeetingScheduled" to isMeetingScheduled,
            "isMeetingAccepted" to isMeetingAccepted,
            "isMeetingRejected" to isMeetingRejected,
            "title" to title,
            "createdAt" to createdAt
        )

        Log.d("MeetingId", meetingId)
        meetingRef.add(meetingObj).addOnCompleteListener {
            callback(true)
        }
    }

    // used to accept the meeting and updating it to DB
    fun acceptMeeting(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore;
        isMeetingScheduled = true
        isMeetingAccepted = true
        isMeetingRejected = false
        val collRef = db.collection("meeting");


        val meetingDetails = hashMapOf<String, Any>(
            "isMeetingScheduled" to isMeetingScheduled,
            "isMeetingAccepted" to isMeetingAccepted,
            "isMeetingRejected" to isMeetingRejected
        )

        collRef.document(meetingId).update(meetingDetails).addOnCompleteListener {
            callback(true);
        }
    }

    // used to decline the meeting and storing it in DB
    fun declineMeeting(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore;
        isMeetingScheduled = true
        isMeetingAccepted = false
        isMeetingRejected = true
        val collRef = db.collection("meeting");

        val meetingDetails = hashMapOf<String, Any>(
            "isMeetingScheduled" to isMeetingScheduled,
            "isMeetingAccepted" to isMeetingAccepted,
            "isMeetingRejected" to isMeetingRejected
        )

        collRef.document(meetingId).update(meetingDetails).addOnCompleteListener {
            callback(true);
        }
    }

}