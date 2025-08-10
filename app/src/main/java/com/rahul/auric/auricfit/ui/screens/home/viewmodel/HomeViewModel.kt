// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/home/viewmodel/HomeViewModel.kt
package com.rahul.auric.auricfit.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rahul.auric.auricfit.data.UserProfileRepository
import com.rahul.auric.auricfit.db.StepData
import com.rahul.auric.auricfit.sensor.StepDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * A data class that represents the entire state of the Home screen.
 * This makes it easy to manage and update the UI from a single state object.
 */
data class HomeUiState(
    // NEW: These are now nullable to represent the initial loading state.
    val steps: Int? = null,
    val distanceKm: Double? = null,
    val caloriesKcal: Double? = null,
    // Goal can have a default, so it doesn't need to be nullable.
    val goal: Int = UserProfileRepository.Defaults.DAILY_STEP_GOAL
)

class HomeViewModel(
    private val userProfileRepository: UserProfileRepository,
    private val stepDataRepository: StepDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // This is the core logic. We use 'combine' to listen to both the user's profile
        // and today's step data. Whenever either of them changes, this block is re-executed.
        viewModelScope.launch {
            combine(
                userProfileRepository.userProfileFlow,
                stepDataRepository.getTodayStepData()
            ) { profile, stepData ->
                // When new data arrives, we create a new UI state.
                createUiState(stepData, profile.dailyStepGoal)
            }.collect { newState ->
                // And update the public _uiState with the new state.
                _uiState.value = newState
            }
        }
    }

    private fun createUiState(stepData: StepData?, goal: Int): HomeUiState {
        return HomeUiState(
            steps = stepData?.steps,
            distanceKm = stepData?.distanceKm,
            caloriesKcal = stepData?.caloriesKcal,
            goal = goal
        )
    }

    /**
     * This function will be called from the UI to start the sensor listening process.
     */
    fun startStepCounting() {
        viewModelScope.launch {
            // We need the user profile to perform calculations, so we get the first available value.
            val userProfile = userProfileRepository.userProfileFlow.first()
            stepDataRepository.startStepCounting(userProfile)
        }
    }

    /**
     * Factory for creating the ViewModel with its dependencies.
     */
    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val userProfileRepository: UserProfileRepository,
        private val stepDataRepository: StepDataRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(userProfileRepository, stepDataRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}