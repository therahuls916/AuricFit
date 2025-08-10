package com.rahul.auric.auricfit.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_step_data")
data class StepData(
    @PrimaryKey
    val date: String, // The date in "YYYY-MM-DD" format, serving as the unique ID

    val steps: Int,
    val distanceKm: Double,
    val caloriesKcal: Double,

    // This is crucial: We store the raw sensor value at the start of the day
    // to correctly calculate steps even after a device reboot.
    val initialSensorValue: Int
)