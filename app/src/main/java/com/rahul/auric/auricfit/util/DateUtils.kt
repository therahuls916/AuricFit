// File: app/src/main/java/com/rahul/auric/auricfit/util/DateUtils.kt
package com.rahul.auric.auricfit.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    // NEW: Format for displaying the month name (e.g., "May")
    private val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

    fun getTodayString(): String {
        return dbDateFormat.format(Date())
    }

    fun formatDateToDay(dateString: String): String {
        return try {
            val date = dbDateFormat.parse(dateString)
            dayFormat.format(date!!)
        } catch (e: Exception) { "?" }
    }

    // NEW: Function to get a unique identifier for the week (e.g., "2024-21" for the 21st week of 2024)
    fun getWeekIdentifier(dateString: String): String {
        return try {
            val date = dbDateFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date!!
            val year = calendar.get(Calendar.YEAR)
            val week = calendar.get(Calendar.WEEK_OF_YEAR)
            "$year-$week"
        } catch (e: Exception) { "" }
    }

    // NEW: Function to get a unique identifier for the month (e.g., "2024-05" for May 2024)
    fun getMonthIdentifier(dateString: String): String {
        return try {
            val date = dbDateFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date!!
            val year = calendar.get(Calendar.YEAR)
            // Month is 0-indexed, so we add 1 and format to two digits
            val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
            "$year-$month"
        } catch (e: Exception) { "" }
    }

    // NEW: Function to format a date string like "2024-05-21" into a month name "May"
    fun formatToMonthName(dateString: String): String {
        return try {
            val date = dbDateFormat.parse(dateString)
            monthFormat.format(date!!)
        } catch (e: Exception) { "?" }
    }
}