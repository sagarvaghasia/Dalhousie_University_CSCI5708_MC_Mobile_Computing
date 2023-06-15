package com.example.group7project.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Matches {

    val db = Firebase.firestore;
    var currentUserEmail: String = String();
    var currentUserType: String = String();
    var targetUserType: String = String();

    constructor() {
        currentUserEmail = CurrentUser.email;
        currentUserType = CurrentUser.userType;
        if (CurrentUser.userType.equals("investor")) {
            targetUserType = "startup";
        } else {
            targetUserType = "investor";
        }
    }

    //gets all the matches for the current user
    fun getAllMatches(callback: (MutableList<Match>) -> Unit) {

        var matchesList = mutableListOf<Match>();
        var matchesIds = mutableListOf<String>();
        val currentUserDocReference = db.collection("users").document(currentUserEmail);
        currentUserDocReference.get()
            .addOnCompleteListener { res ->
                if (res.isSuccessful) {
                    if (!res.result.equals(null)) {
                        val doc = res.result
                        if (doc.data?.get("matches") != null) {
                            matchesIds = doc.data?.get("matches") as ArrayList<String>;
                            if (matchesIds.size > 0) {
                                db.collection(targetUserType)
                                    .whereIn(FieldPath.documentId(), matchesIds)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            var match: Match = Match();
                                            match.email = document.id;
                                            match.name = document.data?.get("name") as String;
                                            match.imageURL =
                                                document.data?.get("imageUrl") as String;
                                            matchesList.add(match);
                                        }
                                        callback(matchesList);
                                    }
                                    .addOnFailureListener { exception ->
                                        callback(matchesList);
                                    }
                            } else
                                callback(matchesList);
                        } else {
                            callback(matchesList);
                        }
                    } else {
                        callback(matchesList);
                    }
                } else {
                    callback(matchesList);
                }
            }
            .addOnFailureListener {
                callback(matchesList);
            }
    }

}