// File: app/src/main/java/com/rahul/auric/auricfit/ui/navigation/AppNavigation.kt
package com.rahul.auric.auricfit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
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
    const val HISTORY = "history"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(Graph.userProfileRepository)
    )
    val userProfile by profileViewModel.userProfile.collectAsState()

    // We need to know if the user has completed the initial profile setup.
    // We use a 'remember' with the userProfile as a key.
    val isProfileSetupComplete = remember(userProfile) {
        userProfile.strideLengthCm != UserProfileRepository.Defaults.STRIDE_LENGTH_CM
    }

    // The start destination is now determined by whether the profile is set up.
    val startDestination = if (isProfileSetupComplete) Routes.HOME else Routes.PROFILE

    Scaffold(
        // The bottom bar is now always visible after the initial setup.
        bottomBar = {
            if (isProfileSetupComplete) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    // NEW: We pass a function to be called after saving.
                    onProfileSaved = {
                        // After saving, navigate to the home screen and clear the back stack
                        // so the user can't press "back" to go to the profile setup again.
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) {
                PermissionHandler(onGranted = {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModel.Factory(
                            userProfileRepository = Graph.userProfileRepository,
                            stepDataRepository = Graph.stepDataRepository
                        )
                    )
                    val uiState by homeViewModel.uiState.collectAsState()
                    LaunchedEffect(Unit) { homeViewModel.startStepCounting() }
                    HomeScreen(
                        steps = uiState.steps,
                        distanceKm = uiState.distanceKm,
                        caloriesKcal = uiState.caloriesKcal,
                        goal = uiState.goal
                    )
                })
            }
            composable(Routes.HISTORY) {
                val historyViewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.Factory(Graph.stepDataRepository)
                )
                val historyData by historyViewModel.historyData.collectAsState()
                val isShowingSteps by historyViewModel.isShowingSteps.collectAsState()
                val timePeriod by historyViewModel.timePeriod.collectAsState()

                HistoryScreen(
                    historyData = historyData,
                    isShowingSteps = isShowingSteps,
                    onDataTypeChange = historyViewModel::onDataTypeChange,
                    timePeriod = timePeriod,
                    onTimePeriodChange = historyViewModel::onTimePeriodChange
                )
            }
        }
    }
}