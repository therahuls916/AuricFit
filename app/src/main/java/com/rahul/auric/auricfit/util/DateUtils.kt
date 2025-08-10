// File: app/src/main/java/com/rahul/auric/auricfit/util/DateUtils.kt
package com.rahul.auric.auricfit.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    // Defines the date format we will use for our database primary key.
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // NEW: Defines the format for displaying the day of the week (e.g., "Mon")
    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

    // A simple function to get the current date as a formatted string.
    fun getTodayString(): String {
        return dbDateFormat.format(Date())
    }

    // NEW: A function to convert a date string like "2024-05-21" to "Tue".
    fun formatDateToDay(dateString: String): String {
        return try {
            val date = dbDateFormat.parse(dateString)
            dayFormat.format(date!!)
        } catch (e: Exception) {
            "?" // Return a placeholder if the date format is wrong
        }
    }
}