package com.example.group7project.model

data class User(
    val email: String? = "",
    val password: String? = "",
    val radio_selected: String? = "",
    val isUserFirstTime: Boolean = true
)