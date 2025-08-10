// File: app/src/main/java/com/rahul/auric/auricfit/data/UserProfileRepository.kt
package com.rahul.auric.auricfit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This creates a DataStore instance at the top level of the file, tied to the application context.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile_settings")

class UserProfileRepository(private val context: Context) {

    // These are the keys we will use to store and retrieve data from DataStore.
    private object PreferencesKeys {
        val WEIGHT_KG = intPreferencesKey("user_weight_kg")
        val STRIDE_LENGTH_CM = intPreferencesKey("user_stride_length_cm")
        val DAILY_STEP_GOAL = intPreferencesKey("user_daily_step_goal")
    }

    // Default values to use if no settings have been saved yet.
    object Defaults {
        const val WEIGHT_KG = 70
        const val STRIDE_LENGTH_CM = 75
        const val DAILY_STEP_GOAL = 10000
    }

    // Expose a Flow of UserProfile data. This Flow will automatically emit a new
    // UserProfile object whenever any of the preference values change.
    val userProfileFlow: Flow<UserProfile> = context.dataStore.data
        .map { preferences ->
            val weight = preferences[PreferencesKeys.WEIGHT_KG] ?: Defaults.WEIGHT_KG
            val stride = preferences[PreferencesKeys.STRIDE_LENGTH_CM] ?: Defaults.STRIDE_LENGTH_CM
            val goal = preferences[PreferencesKeys.DAILY_STEP_GOAL] ?: Defaults.DAILY_STEP_GOAL
            UserProfile(weight, stride, goal)
        }

    /**
     * A suspend function to save the user's profile data.
     * The 'edit' function runs in a transaction to ensure data consistency.
     */
    suspend fun saveUserProfile(weight: Int, stride: Int, goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEIGHT_KG] = weight
            preferences[PreferencesKeys.STRIDE_LENGTH_CM] = stride
            preferences[PreferencesKeys.DAILY_STEP_GOAL] = goal
        }
    }
}