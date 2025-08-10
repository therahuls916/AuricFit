// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/history/HistoryScreen.kt
package com.rahul.auric.auricfit.ui.screens.history

import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.rahul.auric.auricfit.db.StepData
import com.rahul.auric.auricfit.ui.screens.history.viewmodel.TimePeriod
import com.rahul.auric.auricfit.ui.theme.AuricFitTheme
import com.rahul.auric.auricfit.ui.theme.TextGray
import com.rahul.auric.auricfit.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyData: List<StepData>,
    isShowingSteps: Boolean,
    onDataTypeChange: (Boolean) -> Unit,
    timePeriod: TimePeriod,
    onTimePeriodChange: (TimePeriod) -> Unit
) {
    // --- Perform Calculations based on the current state ---
    val chartEntries = remember(historyData, isShowingSteps) {
        historyData.reversed().mapIndexed { index, data ->
            val value = if (isShowingSteps) data.steps.toFloat() else data.caloriesKcal.toFloat()
            BarEntry(index.toFloat(), value)
        }
    }
    val chartLabels = remember(historyData) {
        historyData.reversed().map { DateUtils.formatDateToDay(it.date) }
    }
    val totalValue = remember(historyData, isShowingSteps) {
        if (isShowingSteps) historyData.sumOf { it.steps }.toDouble() else historyData.sumOf { it.caloriesKcal }
    }
    val averageValue = if (historyData.isNotEmpty()) totalValue / historyData.size else 0.0
    val dataTypeLabel = if (isShowingSteps) "Steps" else "Calories"
    val unitLabel = if (isShowingSteps) "steps" else "kCal"


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History", fontWeight = FontWeight.Bold) },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Notifications, contentDescription = "Notifications") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SegmentedControl(
                    options = TimePeriod.values().map { it.name },
                    selectedIndex = timePeriod.ordinal,
                    onSelectionChange = { index -> onTimePeriodChange(TimePeriod.values()[index]) }
                )
            }
            item { DailyTrendChart(entries = chartEntries, labels = chartLabels, dataType = dataTypeLabel) }
            item { ViewOptions(isSteps = isShowingSteps, onToggleChanged = onDataTypeChange) }
            item { SummaryStats(total = totalValue, average = averageValue, unit = unitLabel) }
            item { ExportButton() }
        }
    }
}

// --- CHILD COMPOSABLES ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
             SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onSelectionChange(index) },
                selected = index == selectedIndex
            ) { Text(label) }
        }
    }
}

@Composable
fun DailyTrendChart(entries: List<BarEntry>, labels: List<String>, dataType: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(dataType, style = MaterialTheme.typography.titleLarge)
                Text("Last ${labels.size} days", style = MaterialTheme.typography.bodyMedium, color = TextGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (entries.isNotEmpty()) {
                BarChart(entries = entries, labels = labels)
            }
        }
    }
}

@Composable
fun BarChart(entries: List<BarEntry>, labels: List<String>) {
    val barChartColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

    AndroidView(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false; legend.isEnabled = false
                setDrawGridBackground(false); setDrawBorders(false)
                isDragEnabled = false; isDoubleTapToZoomEnabled = false; setPinchZoom(false)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM; setDrawGridLines(false)
                    this.textColor = textColor; granularity = 1f; labelCount = labels.size.coerceAtLeast(1)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return labels.getOrNull(value.toInt()) ?: ""
                        }
                    }
                }
                axisLeft.apply { axisMinimum = 0f; setDrawGridLines(false); this.textColor = textColor }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val dataSet = BarDataSet(entries, "Data").apply {
                color = barChartColor.toArgb(); setDrawValues(false)
            }
            chart.data = BarData(dataSet).apply { barWidth = 0.5f }
            chart.invalidate()
        }
    )
}

@Composable
fun ViewOptions(isSteps: Boolean, onToggleChanged: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("View Data By:", style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Steps", color = if (isSteps) MaterialTheme.colorScheme.primary else TextGray)
                Switch(
                    checked = !isSteps,
                    onCheckedChange = { onToggleChanged(!it) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text("Calories", color = if (!isSteps) MaterialTheme.colorScheme.primary else TextGray)
            }
        }
    }
}

@Composable
fun SummaryStats(total: Double, average: Double, unit: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(label = "Total", value = "%.0f".format(total), unit = unit, modifier = Modifier.weight(1f))
        StatCard(label = "Average", value = "%.0f".format(average), unit = unit, modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = TextGray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(unit, modifier = Modifier.padding(bottom = 4.dp), style = MaterialTheme.typography.bodyMedium, color = TextGray)
            }
        }
    }
}

@Composable
fun ExportButton() {
    OutlinedButton(
        onClick = { /* TODO */ },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Icon(Icons.Default.Download, contentDescription = "Export CSV", tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Export CSV", color = MaterialTheme.colorScheme.primary)
    }
}


// --- Preview ---

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HistoryScreenInteractivePreview() {
    AuricFitTheme(darkTheme = true) {
        var isShowingSteps by remember { mutableStateOf(true) }
        var timePeriod by remember { mutableStateOf(TimePeriod.Daily) }
        val dummyData = remember {
            listOf(
                StepData("2024-05-21", 8500, 6.3, 390.0, 0),
                StepData("2024-05-20", 10542, 7.9, 420.0, 0),
                StepData("2024-05-19", 6312, 4.2, 215.0, 0),
                StepData("2024-05-18", 1205, 0.9, 55.0, 0),
                StepData("2024-05-17", 7890, 5.1, 350.0, 0)
            )
        }
        HistoryScreen(
            historyData = dummyData,
            isShowingSteps = isShowingSteps,
            onDataTypeChange = { isShowingSteps = it },
            timePeriod = timePeriod,
            onTimePeriodChange = { timePeriod = it }
        )
    }
}