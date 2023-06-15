package com.example.group7project.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.example.group7project.R
import com.example.group7project.`interface`.FragmentNavigation
import com.example.group7project.model.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioInvestor: RadioButton
    private lateinit var radioStartup: RadioButton
    private lateinit var radioSelected: String
    private lateinit var btnRegisterReg: MaterialButton

    private lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_register, container, false)

        radioGroup = view.findViewById(R.id.radio_group)
        radioInvestor = view.findViewById(R.id.radio_investor)
        radioStartup = view.findViewById(R.id.radio_startup)
        email = view.findViewById(R.id.email_reg)
        password = view.findViewById(R.id.password_reg)
        confirmPassword = view.findViewById(R.id.confirm_password_reg)
        btnRegisterReg = view.findViewById(R.id.btn_register_reg)
        fAuth = Firebase.auth

        //Redirecting to login fragment once login button is clicked
        view.findViewById<Button>(R.id.btn_login_reg).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(), false)
        }

        //Registration process takes places once register button is clicked
        view.findViewById<Button>(R.id.btn_register_reg).setOnClickListener {
            validateForm()
        }

        return view
    }

    private fun validateForm() {

        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.warning_ic_icon)
        val emailVar = email.text.toString()
        val passwordVar = password.text.toString()
        val confirmPasswordVar = confirmPassword.text.toString()
        val isUserFirstTime = true

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        //Reference: https://www.youtube.com/watch?v=d3S1AvlxntE&t=919s
        when {

            //validating blank or empty fields in the registration form
            TextUtils.isEmpty(emailVar.trim()) -> {
                email.setError("Please enter email", icon)
            }
            TextUtils.isEmpty(passwordVar.trim()) -> {
                password.setError("Please enter password", icon)
            }
            TextUtils.isEmpty(confirmPasswordVar.trim()) -> {
                confirmPassword.setError("Please enter password again", icon)
            }

            email.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    confirmPassword.text.toString().isNotEmpty() -> {
                //validating email format using regex
                if (email.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    //validating the length of password
                    if (password.text.toString().length >= 8) {
                        //password and confirm password match validation
                        if (password.text.toString() == confirmPassword.text.toString()) {

                            radioSelected = if (radioInvestor.isChecked) {
                                "investor"
                            } else {
                                "startup"
                            }

                            //verifying that the user email already exists in the database
                            val db = FirebaseFirestore.getInstance()
                            val query: Query =
                                db.collection("users").whereEqualTo("email", emailVar)
                                    .whereEqualTo("radio_selected", radioSelected)
                            query.get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (documentSnapshot in task.result!!) {
                                        val userEmail = documentSnapshot.getString("email")
                                        val userType = documentSnapshot.getString("radio_selected")
                                        when {
                                            userEmail == emailVar && userType == radioSelected -> {
                                                Toast.makeText(
                                                    context,
                                                    "Email already exists in database ",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                email.error = "Enter another email address."
                                            }
                                        }
                                    }
                                }
                                if (task.result!!.size() == 0) {
                                    firebaseSignUp()

                                    //Reference: https://www.youtube.com/watch?v=5UEdyUFi_uQ
                                    //saving user details in firestore database.
                                    val user =
                                        User(emailVar, passwordVar, radioSelected, isUserFirstTime)
                                    db.collection("users").document(emailVar).set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Registration Successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Registration Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    redirectToLoginTrue()
                                }
                            }

                        } else {
                            confirmPassword.setError("Password didn't match", icon)
                        }

                    } else {
                        password.error = "Please enter at least 8 characters in password."
                    }
                } else {
                    email.error = "Please enter valid email address."
                }
            }

        }
    }

    //Reference: https://www.youtube.com/watch?v=qpbNe0_RVyk
    //firebase authentication saving email and password
    private fun firebaseSignUp() {
        btnRegisterReg.isEnabled = false
        btnRegisterReg.alpha = 0.5f

        fAuth.createUserWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Register Successful", Toast.LENGTH_SHORT).show()
            } else {
                btnRegisterReg.isEnabled = true
                btnRegisterReg.alpha = 1.0f
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    //redirecting to login fragment after registration
    private fun redirectToLoginTrue() {
        var navRegister = activity as FragmentNavigation
        navRegister.navigateFrag(LoginFragment(), true)
    }

}