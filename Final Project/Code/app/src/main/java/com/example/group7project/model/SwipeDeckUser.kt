package com.example.group7project.model

import java.util.*

/*
    References:
    1. Default user image URL taken from https://previews.123rf.com/images/prettyvectors/prettyvectors1309/prettyvectors130900060/22545994-user-profile-avatar-man-icon.jpg
 */

data class SwipeDeckUser(
    val email: String? = "",
    val type: UserType? = UserType.INVESTOR,
    val name: String = "",
    val description: String = "",
    val isVerified: Boolean = false,
    val imageUrl: String = "https://previews.123rf.com/images/prettyvectors/prettyvectors1309/prettyvectors130900060/22545994-user-profile-avatar-man-icon.jpg",
    val lastActive: Date = Date(2000, 1, 1)
)