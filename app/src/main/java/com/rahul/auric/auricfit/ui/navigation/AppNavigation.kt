// File: app/src/main/java/com/rahul/auric/auricfit/ui/navigation/AppNavigation.kt
package com.rahul.auric.auricfit.ui.navigation

import android.Manifest
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rahul.auric.auricfit.data.UserProfileRepository
import com.rahul.auric.auricfit.di.Graph
import com.rahul.auric.auricfit.ui.screens.history.HistoryScreen
import com.rahul.auric.auricfit.ui.screens.history.viewmodel.HistoryViewModel
import com.rahul.auric.auricfit.ui.screens.home.HomeScreen
import com.rahul.auric.auricfit.ui.screens.home.viewmodel.HomeViewModel
import com.rahul.auric.auricfit.ui.screens.profile.ProfileScreen
import com.rahul.auric.auricfit.ui.screens.profile.viewmodel.ProfileViewModel
import com.rahul.auric.auricfit.ui.screens.splash.SplashScreen
import com.rahul.auric.auricfit.util.PermissionHandler
import android.content.Intent
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import com.rahul.auric.auricfit.sensor.StepCounterService

object Routes {
    const val SPLASH = "splash"
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
    // We collect the state here to pass it to the splash screen logic.
    val userProfile by profileViewModel.userProfile.collectAsState()

    Scaffold(
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            // Only show the bottom bar on the main screens.
            if (currentRoute in listOf(Routes.HOME, Routes.HISTORY, Routes.PROFILE)) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH, // Always start at our custom splash screen
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(onSplashFinished = {
                    // This logic now runs safely after the 2-second delay.
                    val isProfileSetupComplete =
                        userProfile.strideLengthCm != UserProfileRepository.Defaults.STRIDE_LENGTH_CM
                    val nextRoute = if (isProfileSetupComplete) Routes.HOME else Routes.PROFILE
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onProfileSaved = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) {
                val context = LocalContext.current

                // --- THIS IS THE KEY CHANGE ---
                // Create a list of permissions needed for this screen.
                val permissionsToRequest = mutableListOf<String>()
                // Activity Recognition is needed on Android 10+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
                }
                // Post Notifications is needed on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                }

                // Pass the list to our handler
                PermissionHandler(
                    permissions = permissionsToRequest,
                    onGranted = {
                        val homeViewModel: HomeViewModel = viewModel(
                            factory = HomeViewModel.Factory(
                                userProfileRepository = Graph.userProfileRepository,
                                stepDataRepository = Graph.stepDataRepository
                            )
                        )
                        val uiState by homeViewModel.uiState.collectAsState()

                        LaunchedEffect(Unit) {
                            val intent = Intent(context, StepCounterService::class.java).apply {
                                action = StepCounterService.ACTION_START
                            }
                            // On modern Android, we must use startForegroundService
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent)
                            } else {
                                context.startService(intent)
                            }
                        }

                        HomeScreen(
                            steps = uiState.steps,
                            distanceKm = uiState.distanceKm,
                            caloriesKcal = uiState.caloriesKcal,
                            goal = uiState.goal
                        )
                    }
                )
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
                    onTimePeriodChange = historyViewModel::onTimePeriodChange,
                    viewModel = historyViewModel // Ensure the viewModel is passed here
                )
            }
        }
    }
}