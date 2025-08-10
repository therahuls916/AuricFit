// File: app/src/main/java/com/rahul/auric/auricfit/sensor/RebootReceiver.kt
package com.rahul.auric.auricfit.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.rahul.auric.auricfit.di.Graph
import com.rahul.auric.auricfit.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * This BroadcastReceiver is triggered when the device finishes booting.
 * Its purpose is to re-establish the step counter baseline to ensure
 * accurate step counting after a reboot.
 */
class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // We need to initialize the Graph object to access our database
            Graph.provide(context.applicationContext)
            val stepDao = Graph.database.stepDao()
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            if (stepCounterSensor != null) {
                // We need to get a single value from the sensor. We register a listener,
                // wait for the first value, and then immediately unregister it.
                sensorManager.registerListener(
                    object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent?) {
                            event?.let {
                                if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                                    val newBaseline = it.values[0].toInt()
                                    updateDatabaseWithNewBaseline(newBaseline)
                                    // Once we have the value, we don't need the listener anymore.
                                    sensorManager.unregisterListener(this)
                                }
                            }
                        }
                        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                    },
                    stepCounterSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }
    }

    private fun updateDatabaseWithNewBaseline(newBaseline: Int) {
        // We need to run this database operation in a coroutine.
        CoroutineScope(Dispatchers.IO).launch {
            val todayString = DateUtils.getTodayString()
            val todayDbData = Graph.database.stepDao().getStepDataForDate(todayString).first()

            if (todayDbData != null) {
                // If a record for today already exists, update it with the new baseline.
                // The steps are recalculated to be 0 from this new baseline.
                val updatedData = todayDbData.copy(
                    steps = 0,
                    initialSensorValue = newBaseline
                )
                Graph.database.stepDao().upsert(updatedData)
            }
            // If there's no record for today, the regular app flow will create one
            // with the correct baseline when it starts, so we don't need to do anything here.
        }
    }
}