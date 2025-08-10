// File: app/src/main/java/com/rahul/auric/auricfit/ui/navigation/AppNavigation.kt
package com.rahul.auric.auricfit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rahul.auric.auricfit.data.UserProfileRepository
import com.rahul.auric.auricfit.di.Graph
import com.rahul.auric.auricfit.ui.screens.history.HistoryScreen
import com.rahul.auric.auricfit.ui.screens.history.viewmodel.HistoryViewModel
import com.rahul.auric.auricfit.ui.screens.home.HomeScreen
import com.rahul.auric.auricfit.ui.screens.home.viewmodel.HomeViewModel
import com.rahul.auric.auricfit.ui.screens.profile.ProfileScreen
import com.rahul.auric.auricfit.ui.screens.profile.viewmodel.ProfileViewModel
import com.rahul.auric.auricfit.util.PermissionHandler

object Routes {
    const val HOME = "home"
    const val HISTORY = "history" // New route
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(Graph.userProfileRepository)
    )
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()

    val startDestination = if (userProfile.strideLengthCm == UserProfileRepository.Defaults.STRIDE_LENGTH_CM) {
        Routes.PROFILE
    } else {
        Routes.HOME
    }

    // A state to control whether the bottom bar is shown. We hide it on the initial profile setup.
    val showBottomBar = remember(userProfile) {
        userProfile.strideLengthCm != UserProfileRepository.Defaults.STRIDE_LENGTH_CM
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding) // Apply padding from the Scaffold
        ) {
            composable(Routes.PROFILE) {
                ProfileScreen(viewModel = profileViewModel)
            }
            composable(Routes.HOME) {
                PermissionHandler(onGranted = {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModel.Factory(
                            userProfileRepository = Graph.userProfileRepository,
                            stepDataRepository = Graph.stepDataRepository
                        )
                    )
                    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                    LaunchedEffect(Unit) { homeViewModel.startStepCounting() }
                    HomeScreen(
                        steps = uiState.steps,
                        distanceKm = uiState.distanceKm,
                        caloriesKcal = uiState.caloriesKcal,
                        goal = uiState.goal
                    )
                })
            }
            // Add the new History screen destination
            // Update this block
            composable(Routes.HISTORY) {
                val historyViewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.Factory(Graph.stepDataRepository)
                )
                // Collect ALL the state from the ViewModel
                val historyData by historyViewModel.historyData.collectAsStateWithLifecycle()
                val isShowingSteps by historyViewModel.isShowingSteps.collectAsStateWithLifecycle()
                val timePeriod by historyViewModel.timePeriod.collectAsStateWithLifecycle()

                HistoryScreen(
                    // Pass the new dynamic data list
                    historyData = historyData,
                    isShowingSteps = isShowingSteps,
                    onDataTypeChange = historyViewModel::onDataTypeChange,
                    // Pass the new time period state and event handler
                    timePeriod = timePeriod,
                    onTimePeriodChange = { newPeriod ->
                        historyViewModel.onTimePeriodChange(newPeriod)
                    }
                )
            }
        }
    }
}