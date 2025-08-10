// File: app/src/main/java/com/rahul/auric/auricfit/sensor/StepSensorManager.kt
package com.rahul.auric.auricfit.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StepSensorManager(context: Context) : SensorEventListener {

    // Get the Android SensorManager system service
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Get the step counter sensor. It can be null if the device doesn't have one.
    private val stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    // A StateFlow to hold the raw step count from the sensor.
    // It's private to this class but exposed as a read-only StateFlow.
    private val _rawSteps = MutableStateFlow(0)
    val rawSteps = _rawSteps.asStateFlow()

    fun startListening() {
        if (stepCounterSensor == null) {
            Log.w("StepSensorManager", "Step counter sensor not available on this device.")
            return
        }
        // Register this class as a listener for the step counter sensor.
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopListening() {
        // Unregister the listener to save battery when the app is not in use.
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // This method is called whenever the sensor reports a new value.
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                // The step count is the first value in the event array.
                // We update our StateFlow with this new raw value.
                _rawSteps.value = it.values[0].toInt()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // This method is not needed for the step counter, so we can leave it empty.
        Log.i("StepSensorManager", "Sensor accuracy changed to: $accuracy")
    }
}