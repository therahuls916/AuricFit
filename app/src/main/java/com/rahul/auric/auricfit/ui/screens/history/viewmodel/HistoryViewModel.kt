// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/history/viewmodel/HistoryViewModel.kt
package com.rahul.auric.auricfit.ui.screens.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rahul.auric.auricfit.db.StepData
import com.rahul.auric.auricfit.sensor.StepDataRepository
import com.rahul.auric.auricfit.util.DateUtils
import kotlinx.coroutines.flow.*

enum class TimePeriod {
    Daily,
    Weekly,
    Monthly
}

class HistoryViewModel(private val repository: StepDataRepository) : ViewModel() {

    private val _timePeriod = MutableStateFlow(TimePeriod.Daily)
    val timePeriod = _timePeriod.asStateFlow()

    val historyData: StateFlow<List<StepData>> = _timePeriod.flatMapLatest { period ->
        when (period) {
            TimePeriod.Daily -> repository.getWeeklyStepData()
            TimePeriod.Weekly -> aggregateToWeekly(repository.getMonthlyStepData())
            TimePeriod.Monthly -> aggregateToMonthly(repository.getAllStepData())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isShowingSteps = MutableStateFlow(true)
    val isShowingSteps = _isShowingSteps.asStateFlow()

    fun onDataTypeChange(showSteps: Boolean) {
        _isShowingSteps.value = showSteps
    }

    fun onTimePeriodChange(period: TimePeriod) {
        _timePeriod.value = period
    }

    suspend fun getAllDataForExport(): List<StepData> {
        return repository.getAllStepData().first()
    }

    // --- NEW: Aggregation Logic ---

    private fun aggregateToWeekly(dailyDataFlow: Flow<List<StepData>>): Flow<List<StepData>> {
        return dailyDataFlow.map { dailyList ->
            // Group by week identifier (e.g., "2024-21")
            dailyList.groupBy { DateUtils.getWeekIdentifier(it.date) }
                .map { (_, group) ->
                    // For each group, create a single StepData object that sums up the values
                    StepData(
                        date = group.first().date, // Use the date of the first day of the week as the identifier
                        steps = group.sumOf { it.steps },
                        distanceKm = group.sumOf { it.distanceKm },
                        caloriesKcal = group.sumOf { it.caloriesKcal },
                        initialSensorValue = 0 // Not relevant for aggregated data
                    )
                }
        }
    }

    private fun aggregateToMonthly(dailyDataFlow: Flow<List<StepData>>): Flow<List<StepData>> {
        return dailyDataFlow.map { dailyList ->
            // Group by month identifier (e.g., "2024-05")
            dailyList.groupBy { DateUtils.getMonthIdentifier(it.date) }
                .map { (_, group) ->
                    StepData(
                        date = group.first().date, // Use the date of the first day of the month
                        steps = group.sumOf { it.steps },
                        distanceKm = group.sumOf { it.distanceKm },
                        caloriesKcal = group.sumOf { it.caloriesKcal },
                        initialSensorValue = 0
                    )
                }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: StepDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                return HistoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}