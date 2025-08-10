// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/history/viewmodel/HistoryViewModel.kt
package com.rahul.auric.auricfit.ui.screens.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rahul.auric.auricfit.db.StepData
import com.rahul.auric.auricfit.sensor.StepDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

// NEW: Enum to represent the selected time period. This is safer than using integers or strings.
enum class TimePeriod {
    Daily, // Represents the weekly view of daily bars
    Weekly,
    Monthly
}

class HistoryViewModel(private val repository: StepDataRepository) : ViewModel() {

    // --- State for the UI ---

    // NEW: State to track the selected time period. Defaults to Daily (which shows weekly data).
    private val _timePeriod = MutableStateFlow(TimePeriod.Daily)
    val timePeriod = _timePeriod.asStateFlow()

    // NEW: This is a powerful feature of Flow. `flatMapLatest` listens to the _timePeriod flow.
    // Whenever the time period changes, it cancels the old database query and starts a new one.
    // This automatically switches our data source between daily, weekly, and monthly.
    val historyData: StateFlow<List<StepData>> = _timePeriod.flatMapLatest { period ->
        // The 'when' expression now correctly returns a Flow<List<StepData>> in all cases
        when (period) {
            TimePeriod.Daily -> repository.getWeeklyStepData()
            TimePeriod.Weekly -> repository.getMonthlyStepData()
            TimePeriod.Monthly -> repository.getAllStepData() // Typo fixed
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // (For now, Weekly and Monthly will show the same as Daily until we add aggregation logic)

    private val _isShowingSteps = MutableStateFlow(true)
    val isShowingSteps = _isShowingSteps.asStateFlow()


    // --- Events from the UI ---

    fun onDataTypeChange(showSteps: Boolean) {
        _isShowingSteps.value = showSteps
    }

    // NEW: Function called when the user clicks "Daily", "Weekly", or "Monthly".
    fun onTimePeriodChange(period: TimePeriod) {
        _timePeriod.value = period
    }


    // --- Factory ---
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