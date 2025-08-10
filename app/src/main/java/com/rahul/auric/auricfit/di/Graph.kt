// File: app/src/main/java/com/rahul/auric/auricfit/di/Graph.kt
package com.rahul.auric.auricfit.di

import android.content.Context
import com.rahul.auric.auricfit.data.UserProfileRepository
import com.rahul.auric.auricfit.db.AppDatabase
import com.rahul.auric.auricfit.sensor.StepDataRepository
import com.rahul.auric.auricfit.sensor.StepSensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * A simple service locator for providing all app-level dependencies.
 */
object Graph {
    // Database instance
    private lateinit var database: AppDatabase

    // An application-level coroutine scope for background tasks that should not be cancelled.
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // --- Publicly exposed repositories ---
    lateinit var userProfileRepository: UserProfileRepository
        private set
    lateinit var stepDataRepository: StepDataRepository
        private set

    /**
     * This function is called once from the Application class to initialize all dependencies.
     */
    fun provide(context: Context) {
        // Initialize the database
        database = AppDatabase.getDatabase(context)

        // Initialize the repositories
        userProfileRepository = UserProfileRepository(context)

        stepDataRepository = StepDataRepository(
            stepDao = database.stepDao(),
            stepSensorManager = StepSensorManager(context),
            coroutineScope = applicationScope
        )
    }
}