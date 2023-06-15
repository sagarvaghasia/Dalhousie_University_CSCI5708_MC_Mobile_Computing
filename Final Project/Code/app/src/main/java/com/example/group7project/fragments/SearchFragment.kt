package com.example.group7project.fragments

import android.graphics.RenderEffect
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.animation.core.snap
import androidx.constraintlayout.widget.ConstraintLayout

import com.example.group7project.R
import com.example.group7project.adapters.CardsAdapter
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.CurrentUser.email
import com.example.group7project.model.Match
import com.example.group7project.model.SwipeDeckUser
import com.example.group7project.model.UserType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import java.util.Date
import javax.security.auth.callback.Callback

/*
    References:
    1. https://developer.android.com/guide/fragments#topics
    2. https://developer.android.com/develop/ui/views/animations/fling-animation
    3. https://github.com/Diolor/Swipecards
    4. https://www.udemy.com/course/buildtinder/
    5. https://firebase.google.com/docs/firestore/manage-data/add-data#kotlin+ktx
    6. https://www.iconpacks.net
 */

class SearchFragment : Fragment() {

    private lateinit var usersDbRef: CollectionReference
    private lateinit var swipeFrame: SwipeFlingAdapterView
    private var swipeDeckCardsAdapter: ArrayAdapter<SwipeDeckUser>? = null
    private var swipeDeckCards = ArrayList<SwipeDeckUser>()

    // Layout objects
    private lateinit var lookingFor: UserType
    private lateinit var noPotentialUsersFound: TextView
    private lateinit var loadingCards: ProgressBar
    private lateinit var likeButtonRef: AppCompatImageButton
    private lateinit var dislikeButtonRef: AppCompatImageButton

    // Database instance
    val db = FirebaseFirestore.getInstance()

    // User preferences
    var showLeftSwipe: Boolean = false
    var locationPreference: String = "All"
    var categoryPreference: String = "All"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialising layout element references
        swipeFrame = view.findViewById(R.id.frame)
        noPotentialUsersFound = view.findViewById(R.id.noPotentialUsersFound)
        loadingCards = view.findViewById(R.id.loadingCardsProgressIndicator)
        likeButtonRef = view.findViewById(R.id.likeUserButton)
        dislikeButtonRef = view.findViewById(R.id.dislikeUserButton)

        // Initialise db reference
        usersDbRef = FirebaseFirestore.getInstance().collection("users")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user type and update value for looking for
        swipeDeckCardsAdapter = CardsAdapter(requireContext(), R.layout.swipe_item, swipeDeckCards)
        CurrentUser.loadDetails { res -> init() }
    }

    private fun init() {
        lookingFor = if (CurrentUser.userType
                .contains("startup")
        ) UserType.INVESTOR else UserType.START_UP
        val query: Query =
            db.collection(CurrentUser.userType).whereEqualTo("email", CurrentUser.email)

        // Fetch showLeft swipe preference and location preference, if not there use false by default
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (documentSnapshot in task.result!!) {
                    if (documentSnapshot != null) {
                        val element = documentSnapshot.data
                        showLeftSwipe =
                            if (element["showLeftSwipe"] != null) element["showLeftSwipe"] as Boolean else false
                        locationPreference =
                            if (element["locationPreference"] != null) element["locationPreference"] as String else "All"
                        categoryPreference =
                            if (element["categoryPreference"] != null) element["categoryPreference"] as String else "All"
                    }
                }
                // Fetch potential matches for user
                fetchCards()

                // Set last activity time for user
                db.collection(CurrentUser.userType).document(CurrentUser.email)
                    .update("lastActive", Date())

            }
        }


        //
        swipeFrame.adapter = swipeDeckCardsAdapter
        swipeFrame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            // item is removed from the list when item is left or right swiped
            override fun removeFirstObjectInAdapter() {
                swipeDeckCards.removeAt(0)
                swipeDeckCardsAdapter?.notifyDataSetChanged()
                if (swipeDeckCards.isEmpty()) {
                    noPotentialUsersFound.visibility = View.VISIBLE
                }
            }

            // Swipe left action
            override fun onLeftCardExit(p0: Any?) {
                var user = p0 as SwipeDeckUser
                saveLeftSwipeInfo(user)
            }

            // Swipe right action
            override fun onRightCardExit(p0: Any?) {
                saveRightSwipeInfo(p0 as SwipeDeckUser)
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
            }

            override fun onScroll(p0: Float) {
            }
        })

        swipeFrame.setOnItemClickListener { pos, data -> {} }

        dislikeButtonRef.setOnClickListener {
            if (!swipeDeckCards.isEmpty()) {
                swipeFrame.topCardListener.selectLeft()
            }
        }

        likeButtonRef.setOnClickListener {
            if (!swipeDeckCards.isEmpty()) {
                swipeFrame.topCardListener.selectRight()
            }
        }

    }

    // Logic for swipe right functionality
    private fun saveRightSwipeInfo(user: SwipeDeckUser) {
        if (user?.email != null) {
            // Check if "user" has also swiped right
            val query: Query =
                db.collection("users").whereEqualTo("email", user.email)

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (documentSnapshot in task.result!!) {
                        if (documentSnapshot != null) {
                            val element = documentSnapshot.data
                            val userRightSwipes =
                                if (element["rightSwipes"] != null) element["rightSwipes"] as ArrayList<String> else ArrayList()
                            val userLeftSwipes =
                                if (element["leftSwipes"] != null) element["leftSwipes"] as ArrayList<String> else ArrayList()
                            val userMatches =
                                if (element["matches"] != null) element["matches"] as ArrayList<String> else ArrayList()

                            // Case: Other user have swiped right on current user
                            if (userRightSwipes.contains(CurrentUser.email)) {
                                Toast.makeText(context, "It's a match", Toast.LENGTH_LONG).show()

                                userRightSwipes.remove(CurrentUser.email)
                                usersDbRef.document(user.email)
                                    .update("rightSwipes", userRightSwipes)
                                CurrentUser.matches.add(user.email)
                                usersDbRef.document(CurrentUser.email)
                                    .update("matches", CurrentUser.matches)
                                userMatches.add(CurrentUser.email)
                                usersDbRef.document(user.email).update("matches", userMatches)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "DBINFOusers",
                                            "${CurrentUser.email} matched with ${user.email}"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            "DBINFOusers",
                                            "Error writing document",
                                            e
                                        )
                                    }
                            }
                            // Case: Other user has not swiped right on current user
                            else {
                                CurrentUser.rightSwipes.add(user.email)
                                usersDbRef.document(CurrentUser.email)
                                    .update("rightSwipes", CurrentUser.rightSwipes)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "DBINFOusers",
                                            "${CurrentUser.email} swiped right on ${user.email}"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            "DBINFOusers",
                                            "Error writing document",
                                            e
                                        )
                                    }
                            }
                            if (CurrentUser.leftSwipes.contains(user.email)) {
                                CurrentUser.leftSwipes.remove(user.email)
                                usersDbRef.document(CurrentUser.email)
                                    .update("leftSwipes", CurrentUser.leftSwipes)
                            }
                            if (userLeftSwipes.contains(CurrentUser.email)) {
                                userLeftSwipes.remove(CurrentUser.email)
                                usersDbRef.document(user.email).update("leftSwipes", userLeftSwipes)
                            }
                        }
                    }
                }
            }
        }
    }

    // Logic for swipe left functionality
    private fun saveLeftSwipeInfo(user: SwipeDeckUser) {
        if (user?.email != null) {
            if (!CurrentUser.leftSwipes.contains(user.email)) {
                CurrentUser.leftSwipes.add(user.email)
            }
            usersDbRef.document(CurrentUser.email).update("leftSwipes", CurrentUser.leftSwipes)
                .addOnSuccessListener {
                    Log.d(
                        "DBINFOusers",
                        "${CurrentUser.email} swiped left on ${user.email}"
                    )
                }
                .addOnFailureListener { e -> Log.w("DBINFOusers", "Error writing document", e) }
        }
    }

    // Fetch potential matches for current user
    fun fetchCards() {
        swipeDeckCards.clear()

        // Hide "No users found note" and display progress bar
        noPotentialUsersFound.visibility = View.GONE
        loadingCards.visibility = View.VISIBLE

        // Fetch current user preferences: location and category
        val collectionPath = if (lookingFor == UserType.INVESTOR) "investor" else "startup"
        var query: Query = db.collection(collectionPath).whereEqualTo("huntOn", true)
        if (locationPreference != "All") {
            query = query.whereEqualTo("locationPreference", locationPreference)
        }
        if (categoryPreference != "All") {
            query = query.whereEqualTo("categoryPreference", categoryPreference)
        }

        // Execute query to fetch users
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (documentSnapshot in task.result!!) {
                    if (documentSnapshot != null) {
                        val element = documentSnapshot.data
                        var userValid = true
                        // Check if user has already been swiped left, right or matched
                        val checkLeftSwipes =
                            if (showLeftSwipe) false else CurrentUser.leftSwipes.contains(element["email"])
                        if (checkLeftSwipes
                            || CurrentUser.rightSwipes.contains(element["email"])
                            || CurrentUser.matches.contains(element["email"])
                        ) {
                            userValid = false
                        }
                        if (userValid) {
                            val lastActiveUser = documentSnapshot.getTimestamp("lastActive")
                            val lastActiveUserNullSafe =
                                lastActiveUser?.toDate() ?: Date(2000, 1, 1)
                            // Add fetched user to swipe deck
                            swipeDeckCards.add(
                                SwipeDeckUser(
                                    email = element.get("email").toString(),
                                    type = lookingFor,
                                    name = element.get("name").toString(),
                                    description = element.get("description").toString(),
                                    imageUrl = element.get("imageUrl").toString(),
                                    lastActive = lastActiveUserNullSafe
                                )
//                            element.get("imageUrl").toString()
                            )
                            swipeDeckCardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                // Hide progress bar
                loadingCards.visibility = View.GONE
                if (swipeDeckCards.isEmpty()) {
                    noPotentialUsersFound.visibility = View.VISIBLE
                }
            }
        }
    }
}