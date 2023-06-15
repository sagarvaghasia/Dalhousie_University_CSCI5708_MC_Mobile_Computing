package com.example.group7project.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

object CurrentUser {
    var email: String = String();
    var userType: String = String();
    var name: String = String();
    var isuserFirstTime: Boolean = false
    var areDetailsLoaded: Boolean = false;
    var leftSwipes: ArrayList<String> = ArrayList<String>();
    var rightSwipes: ArrayList<String> = ArrayList<String>();
    var matches: ArrayList<String> = ArrayList<String>();
    var lastActive: Date = Date(2000, 1, 1)

    fun getDetails(): CurrentUser {
        if (areDetailsLoaded == false) {
            setDetails() {};
        }
        return this;
    }

    fun setDetails(callback: (Boolean) -> Unit) {
        val user = Firebase.auth.currentUser;
        if (user != null) {
            this.email = user.email.toString();
        }
        areDetailsLoaded = true;
        callback(true);
    }

    fun loadDetails(callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val userRef = db.collection("users").document(email)

        userRef
            .get()
            .addOnCompleteListener { res ->
                if (res.isSuccessful) {
                    if (!res.result.equals(null)) {
                        val doc = res.result
                        userType = doc.data?.get("radio_selected") as String
                        isuserFirstTime = doc.data?.get("userFirstTime") as Boolean
                        if ("leftSwipes" in doc?.data!!) leftSwipes =
                            doc.data?.get("leftSwipes") as ArrayList<String>
                        if ("rightSwipes" in doc?.data!!) rightSwipes =
                            doc.data?.get("rightSwipes") as ArrayList<String>
                        if ("matches" in doc?.data!!) matches =
                            doc.data?.get("matches") as ArrayList<String>
                        if ("lastActive" in doc?.data!!) lastActive =
                            doc.data?.get("lastActive") as Date
                        areDetailsLoaded = true
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

}