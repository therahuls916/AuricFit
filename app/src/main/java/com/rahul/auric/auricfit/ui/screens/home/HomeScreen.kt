// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/home/HomeScreen.kt
package com.rahul.auric.auricfit.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.auric.auricfit.ui.theme.AuricFitTheme
import com.rahul.auric.auricfit.ui.theme.CardDark
import com.rahul.auric.auricfit.ui.theme.PrimaryGreen
import com.rahul.auric.auricfit.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Parameters are now nullable to match the UI State
    steps: Int?,
    distanceKm: Double?,
    caloriesKcal: Double?,
    goal: Int
) {
    // Safely calculate progress, defaulting to 0f if steps are null
    val progress = if (goal > 0 && steps != null) (steps.toFloat() / goal.toFloat()) else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AuricFit", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        // FIX 1: Apply the paddingValues from the Scaffold to the main Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // This uses the padding
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // FIX 2: Pass the nullable 'steps' value down
            StepsHero(steps = steps, goal = goal, progress = progress)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(
                    icon = Icons.Default.DirectionsRun,
                    label = "Distance",
                    // Safely display "--" if value is null
                    value = if (distanceKm != null) "%.1f".format(distanceKm) else "--",
                    unit = "km",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "Calories",
                    // Safely display "--" if value is null
                    value = if (caloriesKcal != null) "%.0f".format(caloriesKcal) else "--",
                    unit = "kcal",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StepsHero(steps: Int?, goal: Int, progress: Float) { // FIX 3: Accept nullable Int? for steps
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = PrimaryGreen.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    // Safely display "--" if steps are null
                    text = if (steps != null) String.format("%,d", steps) else "--",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Steps Today",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressRing(
                    progress = progress,
                    sizeDp = 160.dp,
                    strokeDp = 16.dp
                )
                Text(
                    text = "Daily Goal: ${String.format("%,d", goal)} Steps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }
    }
}

// CircularProgressRing and MetricCard functions do not need to be changed.
// Re-pasting them here for completeness.

@Composable
fun CircularProgressRing(
    progress: Float,
    sizeDp: Dp,
    strokeDp: Dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000), label = "progressAnimation"
    )
    val sweepAngle = animatedProgress * 360f

    val progressBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val progressIndicatorColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = strokeDp.toPx()
            drawArc(
                color = progressBackgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = progressIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun MetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenNewPreview() {
    AuricFitTheme(darkTheme = true) {
        HomeScreen(steps = 8542, distanceKm = 6.2, caloriesKcal = 450.0, goal = 10000)
    }
}