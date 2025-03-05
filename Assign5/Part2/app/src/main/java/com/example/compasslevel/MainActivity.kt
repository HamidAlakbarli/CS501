package com.example.compasslevel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private var azimuth by mutableStateOf(0f)
    private var roll by mutableStateOf(0f)
    private var pitch by mutableStateOf(0f)

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            CompassAndLevelScreen(azimuth, roll, pitch)
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        gyroscope?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravity, 0, event.values.size)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagnetic, 0, event.values.size)
            }
            Sensor.TYPE_GYROSCOPE -> {
                roll = Math.toDegrees(event.values[0].toDouble()).toFloat()
                pitch = Math.toDegrees(event.values[1].toDouble()).toFloat()
            }
        }

        val R = FloatArray(9)
        val I = FloatArray(9)
        if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(R, orientation)
            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun CompassAndLevelScreen(azimuth: Float, roll: Float, pitch: Float) {
    val animatedAzimuth by animateFloatAsState(targetValue = azimuth, label = "Azimuth")
    val animatedRoll by animateFloatAsState(targetValue = roll, label = "Roll")
    val animatedPitch by animateFloatAsState(targetValue = pitch, label = "Pitch")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF90CAF9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Compass", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        CompassDisplay(azimuth = animatedAzimuth)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Digital Level", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        LevelDisplay(roll = animatedRoll, pitch = animatedPitch)
    }
}

@Composable
fun CompassDisplay(azimuth: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(220.dp)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            rotate(-azimuth) {
                drawLine(
                    color = Color.Red,
                    start = center,
                    end = center.copy(y = 20f),
                    strokeWidth = 8f
                )
            }
        }
        Text(text = "N", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        Text(text = "${azimuth.roundToInt()}°", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun LevelDisplay(roll: Float, pitch: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Roll: ${roll.roundToInt()}°", fontSize = 22.sp, color = Color.White)
        Text(text = "Pitch: ${pitch.roundToInt()}°", fontSize = 22.sp, color = Color.White)
    }
}
//Reference : got help from Youtube and Google