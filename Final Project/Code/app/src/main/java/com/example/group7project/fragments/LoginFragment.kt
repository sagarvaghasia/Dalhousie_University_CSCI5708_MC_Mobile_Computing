package com.example.group7project.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.example.group7project.R
import com.example.group7project.`interface`.FragmentNavigation
import com.example.group7project.activities.HomeActivity
import com.example.group7project.model.CurrentUser
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/*Referenced login functionality from the video for learning purposes
: https://www.youtube.com/watch?v=2aML6wfUGGA*/
class LoginFragment : Fragment() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var btn_login: MaterialButton
    private lateinit var login_layout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_login, container, false)

        //initialize the view variables
        email = view.findViewById<TextInputEditText>(R.id.login_email)
        password = view.findViewById<TextInputEditText>(R.id.login_password)
        login_layout = view.findViewById(R.id.login_layout)
        fAuth = Firebase.auth
        btn_login = view.findViewById<MaterialButton>(R.id.btn_login)
        btn_login.setOnClickListener {
            validateForm()
        }
        //navigate to Register
        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }

        return view
    }


    //validates individual field views
    private fun validateForm() {
        Log.i("infotag", "inside validate form")
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.warning_ic_icon)
        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        when {
            TextUtils.isEmpty(email.text.toString().trim()) -> {
                email.setError("Please Enter Email", icon)
                btn_login.isEnabled = true
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please Enter Password", icon)
                btn_login.isEnabled = true
            }

            email.text.toString().isNotEmpty() &&
                    password.text.isNotEmpty() -> {
                //Referenced REGEX string From : https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s01.html
                if (email.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    //passes the control to firebase login function
                    firebaseLogIn()
                } else {
                    email.setError("Please Enter Valid Email", icon)
                }
            }
        }
    }

    //Firebase login function uses the the firebase authetication to signin user with email and password
    private fun firebaseLogIn() {
        Log.i("infotag", "inside firebase login")
        btn_login.isEnabled = false
        btn_login.alpha = 0.5f
        var user: FirebaseUser
        fAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //redirect to new activity
                    user = fAuth.currentUser!!
                    Log.i("infotag_login", "inside task success")
                    Log.i("infotag_login_email", user.email.toString())
                    Log.i("infotag_login_uid", user.uid)
                    CurrentUser.email = user.email.toString()
                    Log.i("InfoCurrentUserEmaLogin", CurrentUser.email)
                    //sets the current user singleton object class to be used in other activities
                    CurrentUser.loadDetails { res -> redirectToHomeActivity() }
                } else {
                    //else case if login is not successful
                    btn_login.isEnabled = true
                    btn_login.alpha = 1f
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    //redirects to home activity
    private fun redirectToHomeActivity() {
        val intent = Intent(activity, HomeActivity::class.java)
        Log.i("infotag", "inside redirect to new activity")
        startActivity(intent)
    }
}