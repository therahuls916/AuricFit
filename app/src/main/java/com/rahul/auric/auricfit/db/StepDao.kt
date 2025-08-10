package com.rahul.auric.auricfit.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    /**
     * Upsert means it will INSERT a new row if the date doesn't exist,
     * or UPDATE the existing row if the date already exists. This is
     * perfect for saving today's progress throughout the day.
     */
    @Upsert
    suspend fun upsert(stepData: StepData)

    /**
     * Retrieves the step data for a specific date. We use a Flow so that
     * the UI can automatically update whenever today's data changes in the database.
     */
    @Query("SELECT * FROM daily_step_data WHERE date = :date")
    fun getStepDataForDate(date: String): Flow<StepData?>

    /**
     * Retrieves the last 7 days of data for the history screen chart.
     * We order by date descending to get the most recent entries.
     */
    @Query("SELECT * FROM daily_step_data ORDER BY date DESC LIMIT 7")
    fun getWeeklyStepData(): Flow<List<StepData>>


    // NEW: Get the last 30 days of data.
    @Query("SELECT * FROM daily_step_data ORDER BY date DESC LIMIT 30")
    fun getMonthlyStepData(): Flow<List<StepData>>

    // NEW: Get all data, which we will group later.
    @Query("SELECT * FROM daily_step_data ORDER BY date DESC")
    fun getAllStepData(): Flow<List<StepData>>
}
