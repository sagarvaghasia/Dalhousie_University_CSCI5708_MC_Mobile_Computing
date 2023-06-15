package com.example.group7project.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.group7project.MainActivity
import com.example.group7project.R
import com.example.group7project.fragments.MatchesFragment
import com.example.group7project.fragments.SearchFragment
import com.example.group7project.model.CurrentUser
import com.example.group7project.`interface`.HomeCallBack
import com.example.group7project.fragments.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.IOException


const val REQUEST_CODE_PHOTO = 1234

// Referred for home activity and layout : https://www.udemy.com/course/buildtinder/learn/lecture/13946844#overview
// this activity will be launched once user successfully logs in
class HomeActivity : AppCompatActivity(), HomeCallBack {

    // these are the three fragments where the activity will navigate to in container view
    private var searchFragment: SearchFragment? = null;
    private var matchesFragment: MatchesFragment? = null;
    private var profileFragment: Fragment? = null;
    private var startupProfileFragment: StartupProfileFragment? = null
    private var investorProfileFragment: InvestorProfileFragment? = null

    var tabLinearLayout: LinearLayout? = null
    var profileTab: TabLayout.Tab? = null
    var searchTab: TabLayout.Tab? = null
    var matchesTab: TabLayout.Tab? = null

    //    var user:User?=null
    private lateinit var fAuth: FirebaseAuth

    private lateinit var userEmail: String
    private var resultImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //get the logged in user
        fAuth = Firebase.auth
        var userFromFireBase = fAuth.currentUser
        userEmail = userFromFireBase?.email.toString()
        Log.i("infoTagUserFromFB", userEmail!!)


        // getting the lab layout
        val tabLayout = findViewById<View>(R.id.navigationTabLayout) as TabLayout;

        tabLinearLayout = tabLayout.getChildAt(0) as LinearLayout?

        // preparing the all three tabs with appropriate tags
        profileTab = tabLayout.newTab();
        profileTab!!.setIcon(R.drawable.ic_baseline_person_outline_24);
        tabLayout.addTab(profileTab!!);

        searchTab = tabLayout.newTab();
        searchTab!!.setIcon(R.drawable.ic_baseline_home_24);
        tabLayout.addTab(searchTab!!);

        matchesTab = tabLayout.newTab();
        matchesTab!!.setIcon(R.drawable.ic_baseline_chat_bubble_24);
        tabLayout.addTab(matchesTab!!);

        Log.i("infoTagCurrentFirstTime", CurrentUser.isuserFirstTime.toString())
        Log.i("infoTagUserType", CurrentUser.userType)
        var isUserFirstTime = CurrentUser.isuserFirstTime
        var userType = CurrentUser.userType
        Log.i("infoTagVARCurFTime", isUserFirstTime.toString())
        Log.i("infoTagVARUserType", userType)

        if (CurrentUser.areDetailsLoaded == false) {
            onSignout()
        }
        var fragmentToLaunch: Fragment? = null;

        // setting correct linkedin profile fragment
        if (userType.equals("investor")) {
            if (investorProfileFragment == null)
                investorProfileFragment = InvestorProfileFragment();
            investorProfileFragment!!.setCallback(this@HomeActivity);
            profileFragment = investorProfileFragment;
        } else if (userType.equals("startup")) {
            if (startupProfileFragment == null)
                startupProfileFragment = StartupProfileFragment();
            startupProfileFragment!!.setCallback(this@HomeActivity);
            profileFragment = startupProfileFragment;
        } else {
            onSignout()
        }

        // adding on tab change listener, which will navigate to the correct fragment according to the tab selected
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(selectedTab: TabLayout.Tab?) {

                // setting the intended tab
                when (selectedTab) {
                    profileTab -> {
                        fragmentToLaunch = profileFragment
                    }
                    searchTab -> {
                        if (searchFragment == null) searchFragment =
                            SearchFragment(); fragmentToLaunch = searchFragment
                    }
                    matchesTab -> {
                        if (matchesFragment == null) matchesFragment =
                            MatchesFragment(); fragmentToLaunch = matchesFragment
                    }
                }
                // calling setFragment() to replace the container view with correct fragment
                if (fragmentToLaunch != null)
                    setFragment(fragmentToLaunch!!);
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })




        Log.i("Currentuseroutsideif", CurrentUser.isuserFirstTime.toString())
        if (CurrentUser.isuserFirstTime) {
            //disable other childs in tab
            tabLinearLayout?.getChildAt(1)?.isClickable = false
            tabLinearLayout?.getChildAt(2)?.isClickable = false

            Log.i("infoTagInsideIf", "inside if is user first time")
            profileTab!!.select()

            if (userType == "investor") {
                Log.i("infoTagIfUserType", "inside if usertype investor")
                if (investorProfileFragment == null) investorProfileFragment =
                    InvestorProfileFragment();
                setFragment(investorProfileFragment!!)
            }
            if (userType == "startup") {
                Log.i("infoTagIfUserType", "inside if usertype startup")
                if (startupProfileFragment == null) startupProfileFragment =
                    StartupProfileFragment();
                setFragment(startupProfileFragment!!)
            }
        } else {
            Log.i("infoinside else", "inside else if user not first time")
            // by default selecting the search tab where all swipe decks will be shown
            tabLinearLayout?.getChildAt(0)?.isClickable = true
            tabLinearLayout?.getChildAt(1)?.isClickable = true
            tabLinearLayout?.getChildAt(2)?.isClickable = true

            searchTab!!.select()

            // by default navigating to search fragment
            if (searchFragment == null) searchFragment = SearchFragment();
            setFragment(searchFragment!!);
        }

    }

    // this function replaces the home page container view with the received fragment
    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.homePageFragmentContainer, fragment)
            .commit();
    }

    override fun onSignout() {
        Firebase.auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        Log.i("infotag", "inside redirect to main activity")
        startActivity(intent)
        this.finish()
    }

    override fun onGetUserId(): String = userEmail!!

    override fun profileComplete() {
        tabLinearLayout?.getChildAt(0)?.isClickable = true
        tabLinearLayout?.getChildAt(1)?.isClickable = true
        tabLinearLayout?.getChildAt(2)?.isClickable = true
        searchTab?.select()
    }


    override fun startActivityForPhoto() {
        Log.i("ImageActivityTag", "inside start activity for photo")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            resultImageUrl = data?.data
            storeImage()
        }
    }

    private fun storeImage() {
        if (resultImageUrl != null && userEmail != null) {
            val filePath =
                FirebaseStorage.getInstance().reference.child("profileImage").child(userEmail)
            var bitmap: Bitmap? = null
            try {
                bitmap =
                    MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()
            var userType = CurrentUser.userType
            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener { e -> e.printStackTrace() }
            uploadTask.addOnSuccessListener { taskSnapshot ->
                filePath.downloadUrl
                    .addOnSuccessListener { uri ->
                        if (userType.equals("investor")) {
                            investorProfileFragment?.updateImageUri(uri.toString())
                        } else {
                            startupProfileFragment?.updateImageUri(uri.toString())
                        }
                    }
                    .addOnFailureListener { e -> e.printStackTrace() }
            }
        }
    }


}