// File: app/src/main/java/com/rahul/auric/auricfit/AuricFitApp.kt
package com.rahul.auric.auricfit

import android.app.Application
import com.rahul.auric.auricfit.di.Graph

class AuricFitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the Graph instance when the application starts
        Graph.provide(this)
    }
}