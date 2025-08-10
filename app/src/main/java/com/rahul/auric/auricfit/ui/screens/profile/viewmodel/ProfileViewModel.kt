// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/profile/viewmodel/ProfileViewModel.kt
package com.rahul.auric.auricfit.ui.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rahul.auric.auricfit.data.UserProfile
import com.rahul.auric.auricfit.data.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    // Load the user profile and expose it as a StateFlow.
    // The UI will collect this StateFlow to get the latest profile data.
    val userProfile: StateFlow<UserProfile> = repository.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            // The flow starts when the UI is visible and stops 5 seconds after it's gone.
            started = SharingStarted.WhileSubscribed(5000),
            // Provide an initial default value while the real data is loading.
            initialValue = UserProfile(
                weightKg = UserProfileRepository.Defaults.WEIGHT_KG,
                strideLengthCm = UserProfileRepository.Defaults.STRIDE_LENGTH_CM,
                dailyStepGoal = UserProfileRepository.Defaults.DAILY_STEP_GOAL
            )
        )

    /**
     * Saves the profile data. This is a suspending function that will be called
     * from a coroutine.
     */
    fun saveProfile(weight: Int, stride: Int, goal: Int) {
        viewModelScope.launch {
            repository.saveUserProfile(weight, stride, goal)
        }
    }

    /**
     * A Factory is needed to create the ViewModel because our ViewModel has a
     * constructor with a parameter (the repository).
     */
    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: UserProfileRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}