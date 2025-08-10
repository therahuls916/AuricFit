// File: app/src/main/java/com/rahul/auric/auricfit/ui/screens/profile/ProfileScreen.kt
package com.rahul.auric.auricfit.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.auric.auricfit.ui.screens.profile.viewmodel.ProfileViewModel
import com.rahul.auric.auricfit.ui.theme.AuricFitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) { // The ViewModel is now a parameter
    val context = LocalContext.current

    // Observe the userProfile StateFlow from the ViewModel
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    // These states hold the text currently in the TextFields.
    // They are separate from the saved profile state.
    var weightState by remember { mutableStateOf(userProfile.weightKg.toString()) }
    var strideState by remember { mutableStateOf(userProfile.strideLengthCm.toString()) }
    var goalState by remember { mutableStateOf(userProfile.dailyStepGoal.toString()) }
    var isDarkTheme by remember { mutableStateOf(true) } // For now, this is just UI

    // This effect updates the text fields ONLY when the saved profile changes.
    // This prevents the cursor from jumping around while the user is typing.
    LaunchedEffect(userProfile) {
        weightState = userProfile.weightKg.toString()
        strideState = userProfile.strideLengthCm.toString()
        goalState = userProfile.dailyStepGoal.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Save Changes") },
                icon = { Icon(Icons.Filled.Check, contentDescription = "Save Changes") },
                onClick = {
                    // Convert text to integers safely, falling back to 0 if invalid
                    val weight = weightState.toIntOrNull() ?: 0
                    val stride = strideState.toIntOrNull() ?: 0
                    val goal = goalState.toIntOrNull() ?: 0
                    // Call the ViewModel's save function
                    viewModel.saveProfile(weight, stride, goal)
                    // Show a confirmation message
                    Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Physical Metrics Section ---
            Text("Physical Metrics", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileTextField(
                        label = "Weight",
                        value = weightState,
                        onValueChange = { weightState = it },
                        unit = "kg",
                        modifier = Modifier.weight(1f)
                    )
                    ProfileTextField(
                        label = "Stride Length",
                        value = strideState,
                        onValueChange = { strideState = it },
                        unit = "cm",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Daily Goals Section ---
            Text("Daily Goals", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileTextField(
                        label = "Steps Goal",
                        value = goalState,
                        onValueChange = { goalState = it },
                        unit = "steps"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- App Preferences Section ---
            Text("App Preferences", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp)) // Spacer for the FAB
        }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        suffix = { Text(unit, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        )
    )
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    AuricFitTheme(darkTheme = true) {
        // We can't preview the full screen with ViewModel easily,
        // so we'll just show the basic UI structure for the preview.
        // The real test is running on the device.
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Text("Profile Screen Preview", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}