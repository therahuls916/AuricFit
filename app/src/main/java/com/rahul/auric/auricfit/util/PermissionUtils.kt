// File: app/src/main/java/com/rahul/auric/auricfit/util/PermissionUtils.kt
package com.rahul.auric.auricfit.util


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * A wrapper composable that handles the ACTIVITY_RECOGNITION permission flow.
 * @param onGranted The content to show when the permission has been granted.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissions: List<String>, // Now accepts a list of permissions
    onGranted: @Composable () -> Unit,
) {
    // If the list is empty (for older Android versions), grant immediately.
    if (permissions.isEmpty()) {
        onGranted()
        return
    }

    val permissionStates = rememberMultiplePermissionsState(permissions = permissions)

    if (permissionStates.allPermissionsGranted) {
        onGranted()
    } else {
        // Pass the states to the request screen
        PermissionRequestScreen(permissionStates)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRequestScreen(permissionStates: MultiplePermissionsState) { // Updated parameter
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Permissions Required",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "AuricFit needs the following permissions to function correctly:\n\n• Physical Activity (to count steps)\n• Notifications (to show tracking status)",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { permissionStates.launchMultiplePermissionRequest() }, // Use the multiple request launcher
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Permissions")
        }
    }
}