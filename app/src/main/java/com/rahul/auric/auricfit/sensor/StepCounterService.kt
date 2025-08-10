// File: app/src/main/java/com/rahul/auric/auricfit/sensor/StepCounterService.kt
package com.rahul.auric.auricfit.sensor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.rahul.auric.auricfit.R
import com.rahul.auric.auricfit.di.Graph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StepCounterService : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "StepCounterChannel"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startService()
            ACTION_STOP -> stopService()
        }
        return START_STICKY // If the service is killed, the system will try to restart it.
    }

    private fun startService() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("AuricFit is Active")
            .setContentText("Tracking your steps in the background")
            .setSmallIcon(R.drawable.ic_auricfit_logo_foreground) // Use the icon we created
            .setOngoing(true) // Makes the notification non-dismissible
            .build()

        startForeground(NOTIFICATION_ID, notification)

        // Start listening to steps
        scope.launch {
            val userProfile = Graph.userProfileRepository.userProfileFlow.first()
            Graph.stepDataRepository.startStepCounting(userProfile)
        }
    }

    private fun stopService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf() // Stops the service
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Step Counter",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Cancel all coroutines when the service is destroyed
    }

    // We don't need to bind to this service, so we return null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}