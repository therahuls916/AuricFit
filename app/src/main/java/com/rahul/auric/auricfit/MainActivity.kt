// File: app/src/main/java/com/rahul/auric/auricfit/MainActivity.kt
package com.rahul.auric.auricfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rahul.auric.auricfit.ui.navigation.AppNavigation
import com.rahul.auric.auricfit.ui.theme.AuricFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuricFitTheme {
                AppNavigation()
            }
        }
    }
}