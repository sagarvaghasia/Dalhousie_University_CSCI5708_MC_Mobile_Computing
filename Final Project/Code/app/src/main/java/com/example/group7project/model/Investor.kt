package com.example.group7project.model

data class Investor(
    val email: String? = "",
    val name: String? = "",
    val description: String? = "",
    val imageUrl: String? = "",
    val huntOn: Boolean = true,
    val locationPreference: String? = "",
    val categoryPreference: String? = "",
    val isProfileComplete: Boolean = false,
    val showLeftSwipe: Boolean = false
)
