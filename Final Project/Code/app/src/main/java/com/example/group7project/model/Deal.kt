package com.example.group7project.model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Deal {
    var dealId: String = String();
    var currentUser: String = String();
    var investor: String = String();
    var startup: String = String();
    var termsAndConditions: String = String();
    var amount: String = String();
    var dealOffered: Boolean = false;
    var offeredBy: String = String();
    var dealCompleted: Boolean = false;
    var dealRejected: Boolean = false;

    constructor() {
        val user = Firebase.auth.currentUser;
        if (user != null) {
            this.currentUser = user.email.toString();
        }
    }

    // loads the deal details between the current user and the received user
    fun loadDetails(secondaryUserEmail: String, callback: (Boolean) -> Unit) {
        if (CurrentUser.userType.equals("investor")) {
            this.investor = CurrentUser.email;
            this.startup = secondaryUserEmail;
        } else {
            this.investor = secondaryUserEmail;
            this.startup = CurrentUser.email;
        }

        val db = Firebase.firestore;
        var emailList: ArrayList<String> = ArrayList();

        val dealsRef = db.collection("deals");

        dealsRef.whereEqualTo("investor", investor).whereEqualTo("startup", startup).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // setting the deal detailss
                    dealId = document.id;
                    termsAndConditions = document.data?.get("termsAndConditions") as String;
                    amount = document.data?.get("amount") as String;
                    dealOffered = document.data?.get("dealOffered") as Boolean;
                    offeredBy = document.data?.get("offeredBy") as String;
                    dealCompleted = document.data?.get("dealCompleted") as Boolean;
                    dealRejected = document.data?.get("dealRejected") as Boolean;
                    callback(true);
                }

                // will create a new deal entry if it's not present
                if (result.size() == 0) {
                    dealOffered = false;
                    dealCompleted = false;
                    dealRejected = false;
                    var deal = hashMapOf(
                        "investor" to investor,
                        "startup" to startup,
                        "termsAndConditions" to termsAndConditions,
                        "amount" to amount,
                        "offeredBy" to offeredBy,
                        "dealOffered" to dealOffered,
                        "dealCompleted" to dealCompleted,
                        "dealRejected" to dealRejected
                    );
                    db.collection("deals").add(deal)
                        .addOnSuccessListener { documentReference ->
                            this.dealId = documentReference.id;
                            Log.d(
                                "SUCCESS",
                                "DocumentSnapshot written with ID: ${documentReference.id}"
                            )
                            callback(true);
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                            callback(false);
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR", "Error getting documents: ", exception)
            }
    }

    // creates a new deal between the current user and the matched user.
    fun createDeal(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore;
        dealOffered = true;
        dealCompleted = false;
        dealRejected = false;
        val collRef = db.collection("deals");

        // Update the timestamp field with the value from the server
        val dealDeatils = hashMapOf<String, Any>(
            "investor" to investor,
            "startup" to startup,
            "termsAndConditions" to termsAndConditions,
            "amount" to amount,
            "offeredBy" to offeredBy,
            "dealOffered" to dealOffered,
            "dealCompleted" to dealCompleted,
            "dealRejected" to dealRejected
        )

        // creates a new deal between the current user and the matched user if it doesn't exist, else it updates.
        collRef.document(dealId).update(dealDeatils).addOnCompleteListener {
            callback(true);
        }

    }

    // this function accepts the deal
    fun acceptDeal(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore;
        dealOffered = true;
        dealCompleted = true;
        dealRejected = false;
        val collRef = db.collection("deals");

        // Update the deal completed and other affected fields
        val dealDeatils = hashMapOf<String, Any>(
            "dealOffered" to dealOffered,
            "dealCompleted" to dealCompleted,
            "dealRejected" to dealRejected
        )

        collRef.document(dealId).update(dealDeatils).addOnCompleteListener {
            callback(true);
        }
    }

    // this function sets the deal to be rejected
    fun rejectDeal(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore;
        dealOffered = true;
        dealCompleted = false;
        dealRejected = true;
        val collRef = db.collection("deals");

        // Update the deal rejcetd and other affected fields
        val dealDeatils = hashMapOf<String, Any>(
            "dealOffered" to dealOffered,
            "dealCompleted" to dealCompleted,
            "dealRejected" to dealRejected
        )

        collRef.document(dealId).update(dealDeatils).addOnCompleteListener {
            callback(true);
        }
    }

}