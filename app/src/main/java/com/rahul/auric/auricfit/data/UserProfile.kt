package com.rahul.auric.auricfit.data

/**
 * A simple data class to hold the user's profile information.
 * This makes it easy to pass user settings around the app.
 */
data class UserProfile(
    val weightKg: Int,
    val strideLengthCm: Int,
    val dailyStepGoal: Int,
    // We can add more settings here later, like dark mode preference.
)