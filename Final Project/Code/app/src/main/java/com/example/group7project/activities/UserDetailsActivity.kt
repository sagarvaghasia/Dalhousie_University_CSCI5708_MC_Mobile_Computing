package com.example.group7project.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.group7project.R
import com.example.group7project.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class UserDetailsActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        var image = findViewById<ImageView>(R.id.userDetailsPic)
        val userEmail = intent.extras?.getString(USER_EMAIL, "")
        if (userEmail.isNullOrEmpty()) {
            finish()
        }
        val reqTable = if (CurrentUser.userType == "startup") "investor" else "startup"
        val query: Query = db.collection(reqTable).whereEqualTo("email", userEmail)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (documentSnapshot in task.result!!) {
                    if (documentSnapshot != null) {
                        val element = documentSnapshot.data
                        findViewById<TextView>(R.id.userDetailsName).text =
                            element["name"] as String
                        findViewById<TextView>(R.id.userDetailsDesc).text =
                            element["description"] as String
                        findViewById<TextView>(R.id.preferenceCategoryValue).text =
                            element["categoryPreference"] as String
                        findViewById<TextView>(R.id.locationValue).text =
                            element["locationPreference"] as String
//                        val startUpConstraint = findViewById<ConstraintLayout>(R.id.startUpFields)
                        if (CurrentUser.userType == "startup") {
//                            startUpConstraint.visibility = View.GONE
                            findViewById<TextView>(R.id.fundingAttainedValue).visibility = View.GONE
                            findViewById<TextView>(R.id.fundingAttainedLabel).visibility = View.GONE
                            findViewById<TextView>(R.id.fundingReqLabel).visibility = View.GONE
                            findViewById<TextView>(R.id.fundingReqValue).visibility = View.GONE
                        } else {
//                            startUpConstraint.visibility = View.VISIBLE
                            findViewById<TextView>(R.id.fundingAttainedValue).text =
                                element["fundingAttained"].toString()
                            findViewById<TextView>(R.id.fundingReqValue).text =
                                element["fundingRequired"].toString()
                        }
                        val imageUrl = element["imageUrl"]
                        if (imageUrl != null) {
                            Glide.with(this@UserDetailsActivity)
                                .load(imageUrl)
                                .into(image)
                        }
                    }
                }
            }
        }

    }

    companion object {
        private val USER_EMAIL = "user Email"
        fun newIntent(context: Context, email: String?): Intent {
            val intent = Intent(context, UserDetailsActivity::class.java)
            intent.putExtra(USER_EMAIL, email)
            return intent
        }
    }
}