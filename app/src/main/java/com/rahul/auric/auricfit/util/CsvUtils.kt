// File: app/src/main/java/com/rahul/auric/auricfit/util/CsvUtils.kt
package com.rahul.auric.auricfit.util

import com.rahul.auric.auricfit.db.StepData

object CsvUtils {

    /**
     * Converts a list of StepData objects into a CSV-formatted string.
     * @param data The list of step data to convert.
     * @return A string containing the data in CSV format.
     */
    fun toCsv(data: List<StepData>): String {
        val stringBuilder = StringBuilder()
        // Append the CSV header
        stringBuilder.append("Date,Steps,Distance (km),Calories (kCal)\n")

        // Append each row of data
        data.forEach { stepData ->
            stringBuilder.append("${stepData.date},${stepData.steps},${"%.2f".format(stepData.distanceKm)},${"%.0f".format(stepData.caloriesKcal)}\n")
        }

        return stringBuilder.toString()
    }
}