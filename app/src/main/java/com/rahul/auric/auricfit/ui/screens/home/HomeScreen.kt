// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/home/HomeScreen.kt
package com.rahul.auric.auricfit.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.auric.auricfit.ui.theme.AuricFitTheme
import com.rahul.auric.auricfit.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // We'll pass the state from the ViewModel later
    steps: Int,
    distanceKm: Double,
    caloriesKcal: Double,
    goal: Int
) {
    val progress = if (goal > 0) steps.toFloat() / goal.toFloat() else 0f

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
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main steps and progress ring card
            StepsHero(steps = steps, goal = goal, progress = progress)

            Spacer(modifier = Modifier.height(32.dp))

            // Distance and Calories cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(
                    label = "Distance",
                    value = "%.2f".format(distanceKm),
                    unit = "km",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Calories",
                    value = "%.0f".format(caloriesKcal),
                    unit = "kcal",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StepsHero(steps: Int, goal: Int, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$steps",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Steps Today",
                style = MaterialTheme.typography.titleLarge,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressRing(
                progress = progress,
                sizeDp = 180.dp,
                strokeDp = 18.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Daily Goal: $goal Steps",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray
            )
        }
    }
}

@Composable
fun CircularProgressRing(
    progress: Float,
    sizeDp: Dp,
    strokeDp: Dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    val sweepAngle = animatedProgress * 360f

    // --- FIX IS HERE ---
    // Read the colors OUTSIDE the Canvas block, while we are still in a @Composable context.
    val progressBackgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val progressIndicatorColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = strokeDp.toPx()
            // Background ring
            drawArc(
                color = progressBackgroundColor, // Use the variable
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Foreground progress arc
            drawArc(
                color = progressIndicatorColor, // Use the variable
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = textColor // Use the variable
        )
    }
}

@Composable
fun MetricCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        // All Text composables must be inside this Column's scope.
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Combine label and unit into one for better alignment
            Text(
                text = "$label ($unit)",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AuricFitTheme(darkTheme = true) {
        HomeScreen(steps = 8542, distanceKm = 6.2, caloriesKcal = 450.0, goal = 10000)
    }
}