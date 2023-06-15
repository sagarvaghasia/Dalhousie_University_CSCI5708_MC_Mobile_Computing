package com.example.group7project.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.group7project.R
import com.example.group7project.`interface`.HomeCallBack
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.Startup
import com.example.group7project.model.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


// Referred for home activity and layout : https://www.udemy.com/course/buildtinder/learn/lecture/13946844#overview
class StartupProfileFragment : Fragment() {
    lateinit var location: String
    lateinit var category: String
    lateinit var photoIV: ImageView
    lateinit var nameET: EditText
    lateinit var descriptionET: EditText
    lateinit var fundingET: EditText
    lateinit var switchHunt: SwitchMaterial
    lateinit var switchShowLeftSwipe: SwitchMaterial
    var imageUrl: String? = ""
    lateinit var spinnerLocation: Spinner
    lateinit var spinnerCategory: Spinner
    lateinit var progressLayout: LinearLayout
    lateinit var logoutButton: MaterialButton;
    lateinit var saveButtonButton: MaterialButton;
    var switchLeftSwipe: Boolean = true
    var switchResult: Boolean = true

    //static list for locations
    var locations = arrayOf("All", "Halifax", "Montreal", "Toronto")

    //static list for categories
    var categories = arrayOf("All", "IT and consulting", "Automotive", "Food and Beverages")

    //initialize alert dailog builder
    private lateinit var builder: AlertDialog.Builder

    //homecallback is interface that is implemented in HomeActivity
    private var callback: HomeCallBack? = null
    fun setCallback(callback: HomeCallBack) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_startup_profile, container, false)

        //initialize view variables
        photoIV = view.findViewById(R.id.photoIV)
        nameET = view.findViewById(R.id.nameET)
        descriptionET = view.findViewById(R.id.descriptionET)
        fundingET = view.findViewById(R.id.fundingET)

        builder = AlertDialog.Builder(requireContext())
        spinnerLocation = view.findViewById<Spinner>(R.id.spinner_location)
        val arrayadapterLocation = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            locations
        )
        spinnerLocation.adapter = arrayadapterLocation
        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                location = locations[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                location = locations[0]
            }
        }

        spinnerCategory = view.findViewById<Spinner>(R.id.spinner_category)
        val arrayadapterCategory = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        spinnerCategory.adapter = arrayadapterCategory
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                category = categories[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                category = locations[0]
            }
        }

        switchHunt = view.findViewById(R.id.switch_hunt)
        switchHunt?.setOnCheckedChangeListener { _, isChecked ->
            switchResult = isChecked
        }

        switchShowLeftSwipe = view.findViewById(R.id.switch_leftSwipe)
        switchShowLeftSwipe?.setOnCheckedChangeListener { _, isChecked ->
            switchLeftSwipe = isChecked
        }

        logoutButton = view.findViewById<MaterialButton>(R.id.btn_logout_startup);
        saveButtonButton = view.findViewById<MaterialButton>(R.id.btn_save_startup);

        photoIV.setOnClickListener { callback?.startActivityForPhoto() }

        saveButtonButton.setOnClickListener {
            Log.i("SaveButtonTag", "inside saveButton")
            onSave()
        }
        logoutButton.setOnClickListener {
            Log.i("LogoutButtonTag", "inside logout Button")
            onLogout()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressLayout = view.findViewById<LinearLayout>(R.id.progressLayout)
        progressLayout.setOnTouchListener { view, event -> true }

        //if condition to check if the user if first time on this screen then do not call populate info ,else call populate info
        val userFirstTime = CurrentUser.isuserFirstTime
        Log.i("infoTagUserFirst", userFirstTime.toString())
        if (userFirstTime) {
            Log.i("visibiltyGoneTag", "visibility Gone")
            progressLayout.visibility = View.GONE
        } else {
            populateInfo()
        }
    }


    //populate info
    fun populateInfo() {
        progressLayout.visibility = View.VISIBLE
        // get data from db and populate the screen
        val db = Firebase.firestore
        var email = callback?.onGetUserId()
        Log.i("email to string", email.toString())
        val docRef = db.collection("startup").document(email.toString())
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val startup = documentSnapshot.toObject<Startup>()
            nameET.setText(startup?.name, TextView.BufferType.EDITABLE)
            descriptionET.setText(startup?.description, TextView.BufferType.EDITABLE)
            fundingET.setText(startup?.fundingRequired.toString(), TextView.BufferType.EDITABLE)
            if (startup?.huntOn == true) {
                switchHunt.isChecked = true
            } else {
                switchHunt.isChecked = false
            }

            if (startup?.showLeftSwipe == true) {
                switchShowLeftSwipe.isChecked = true
            } else {
                switchShowLeftSwipe.isChecked = false
            }

            spinnerLocation.setSelection(locations.indexOf(startup?.locationPreference), true)
            spinnerCategory.setSelection(categories.indexOf(startup?.categoryPreference), true)

            if (!startup?.imageUrl.isNullOrEmpty()) {
                imageUrl = startup?.imageUrl
                populateImage(startup?.imageUrl!!)
            }
            progressLayout.visibility = View.GONE
        }
    }

    //update image uri received from the startActivityForPhoto
    fun updateImageUri(uri: String) {
        //update DB with(uri) url of image
        imageUrl = uri
        populateImage(uri)
    }

    //populates the image based on the uri
    fun populateImage(uri: String) {
        Glide.with(this)
            .load(uri)
            .into(photoIV)
    }

    //trigger by save button
    private fun onSave() {
        //get data from fragment and save in db
        val db = Firebase.firestore
        val (isNullOrEmpty, message) = checkForIsNullOrEmpty()
        if (isNullOrEmpty) {
            builder.setTitle("Required : ")
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton("OK") { dailogInterface, it ->
                    dailogInterface.cancel()
                }
                .show()
        } else {
            val email = callback?.onGetUserId()
            Log.i("infoTagEmailInOnsave", email!!)
            val name = nameET.text.toString()
            val description = descriptionET.text.toString()
            val isHuntOn = switchResult
            val locationPreference = location
            val categoryPreference = category
            val fundingRequired = fundingET.text.toString().toLong()
            val isProfileComplete = true
            val imageUrl = imageUrl
            val showLeftSwipe = switchLeftSwipe
            val startup = Startup(
                email,
                name,
                description,
                imageUrl,
                isHuntOn,
                locationPreference,
                categoryPreference,
                fundingRequired,
                0,
                isProfileComplete,
                showLeftSwipe
            )

            //save to db
            db.collection("startup").document(email).set(startup)
                .addOnSuccessListener {
                    Log.d(
                        "DBINFOStartup",
                        "DocumentSnapshot successfully written!"
                    )
                }
                .addOnFailureListener { e -> Log.w("DBINFOStartup", "Error writing document", e) }
            //updates the user first time variable to false after the details are saved for first time
            db.collection("users").document(email).update("userFirstTime", false)
                .addOnSuccessListener {
                    Log.d(
                        "DBINFOusers",
                        "DocumentSnapshot successfully updated!"
                    )
                }
                .addOnFailureListener { e -> Log.w("DBINFOusers", "Error writing document", e) }
            //updates the singleton current user
            CurrentUser.isuserFirstTime = false
            Log.i("currUFirstTimeinSave", CurrentUser.isuserFirstTime.toString())
            callback?.profileComplete()
        }
    }

    //on logout function callbacks on sign out
    private fun onLogout() {
        callback?.onSignout()
    }

    //checks for null or empty values on views
    private fun checkForIsNullOrEmpty(): Pair<Boolean, String> {
        var message = StringBuilder()
        var result = false
        if (nameET.text.toString().isNullOrEmpty()) {
            result = true
            message.append(" - Name").append("\n")

        }
        if (descriptionET.text.toString().isNullOrEmpty()) {
            result = true
            message.append(" - Description").append("\n")
        }
        if (fundingET.text.toString().isNullOrEmpty()) {
            result = true
            message.append(" - Funding").append("\n")
        }
        if (location.isNullOrEmpty()) {
            result = true
            message.append(" - Location").append("\n")
        }
        if (category.isNullOrEmpty()) {
            result = true
            message.append(" - Category").append("\n")
        }
        if (imageUrl.isNullOrEmpty()) {
            result = true
            message.append(" - Display Picture").append("\n")
        }

        return return Pair(result, message.toString())
    }
}