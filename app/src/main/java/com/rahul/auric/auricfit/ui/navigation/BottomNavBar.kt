// File: app/src/main/java/com/rahul/auric/auricfit/ui/navigation/BottomNavBar.kt
package com.rahul.auric.auricfit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Data class to represent a navigation item
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, Routes.HOME),
        BottomNavItem("History", Icons.Default.History, Routes.HISTORY),
        BottomNavItem("Profile", Icons.Default.Person, Routes.PROFILE)
    )

    NavigationBar {
        val backStackEntry = navController.currentBackStackEntryAsState()

        navItems.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) { // Avoid re-navigating to the same screen
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to avoid building up a large back stack
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = "${item.label} Icon") }
            )
        }
    }
}