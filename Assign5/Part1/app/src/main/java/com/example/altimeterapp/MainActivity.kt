package com.example.altimeterapp
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null

    private var pressure by mutableStateOf(1013.25f)
    private var altitude by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        setContent {
            AltimeterScreen(pressure, altitude)
        }
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.firstOrNull()?.let { newPressure ->
            pressure = newPressure
            altitude = calculateAltitude(newPressure)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun calculateAltitude(pressure: Float, p0: Float = 1013.25f): Float {
        return 44330 * (1 - (pressure / p0).pow(1 / 5.255f))
    }
}

@Composable
fun AltimeterScreen(pressure: Float, altitude: Float) {
    val backgroundColor = when {
        altitude < 100 -> Color(0xFFB3E5FC)
        altitude < 500 -> Color(0xFF81D4FA)
        altitude < 1000 -> Color(0xFF4FC3F7)
        altitude < 2000 -> Color(0xFF039BE5) // Dark blue
        else -> Color(0xFF01579B)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Pressure: $pressure hPa",
                fontSize = 24.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Altitude: ${altitude.toInt()} m",
                fontSize = 30.sp,
                color = Color.Black
            )
        }
    }
}
