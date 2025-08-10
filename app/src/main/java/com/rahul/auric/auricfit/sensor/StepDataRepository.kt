// File: app/src/main/java/com/rahul/auric/auricfit/sensor/StepDataRepository.kt
package com.rahul.auric.auricfit.sensor

import com.rahul.auric.auricfit.data.UserProfile
import com.rahul.auric.auricfit.db.StepDao
import com.rahul.auric.auricfit.db.StepData
import com.rahul.auric.auricfit.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StepDataRepository(
    private val stepDao: StepDao,
    private val stepSensorManager: StepSensorManager,
    private val coroutineScope: CoroutineScope // We'll pass a scope from our Graph
) {
    private val todayString = DateUtils.getTodayString()

    // Public functions to expose data from the DAO
    fun getTodayStepData(): Flow<StepData?> = stepDao.getStepDataForDate(todayString)
    fun getWeeklyStepData(): Flow<List<StepData>> = stepDao.getWeeklyStepData()
    fun getMonthlyStepData(): Flow<List<StepData>> = stepDao.getMonthlyStepData() // NEW
    fun getAllStepData(): Flow<List<StepData>> = stepDao.getAllStepData()
    fun startStepCounting(userProfile: UserProfile) {
        stepSensorManager.startListening()

        coroutineScope.launch {
            // Collect raw steps from the sensor
            stepSensorManager.rawSteps.collect { rawSteps ->
                if (rawSteps > 0) { // Ignore initial zero value from flow
                    processNewStepCount(rawSteps, userProfile)
                }
            }
        }
    }

    private suspend fun processNewStepCount(rawSteps: Int, userProfile: UserProfile) {
        val todayDbData = stepDao.getStepDataForDate(todayString).first()

        // If there's no entry for today, the current raw step count is our baseline.
        // Otherwise, use the baseline we stored earlier.
        val initialSensorValue = todayDbData?.initialSensorValue ?: rawSteps

        // Today's steps = current raw value - baseline for today.
        val todaySteps = rawSteps - initialSensorValue

        if (todaySteps < 0) return // Can happen on device reboot, we'll handle this later

        // --- Perform Calculations ---
        val stepLengthMeters = userProfile.strideLengthCm / 100.0
        val distanceKm = (todaySteps * stepLengthMeters) / 1000.0

        // MET formula: Calories = MET * weight(kg) * time(hr)
        // We assume an average walking MET of 3.5 and speed of 5 km/h
        val MET = 3.5
        val speedKmh = 5.0
        val timeHours = if (speedKmh > 0) distanceKm / speedKmh else 0.0
        val caloriesKcal = MET * userProfile.weightKg * timeHours

        // Create the new data object to save
        val newStepData = StepData(
            date = todayString,
            steps = todaySteps,
            distanceKm = distanceKm,
            caloriesKcal = caloriesKcal,
            initialSensorValue = initialSensorValue // IMPORTANT: We save the baseline
        )

        // Save the updated data to the database
        stepDao.upsert(newStepData)
    }
}