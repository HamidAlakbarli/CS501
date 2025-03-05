package com.example.gyroballgame

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var ballX by mutableStateOf(0f)
    private var ballY by mutableStateOf(0f)
    private val ballRadius = 30f
    private var screenWidth by mutableStateOf(1f)
    private var screenHeight by mutableStateOf(1f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            BallGameScreen(
                ballX = ballX,
                ballY = ballY,
                ballRadius = ballRadius,
                onScreenSizeChange = { width, height ->
                    screenWidth = width
                    screenHeight = height
                    ballX = width / 2
                    ballY = height / 2
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && screenWidth > 1 && screenHeight > 1) {
            val xTilt = -event.values[0]
            val yTilt = event.values[1]

            val sensitivity = 5f

            ballX = min(max(ballX + xTilt * sensitivity, ballRadius), screenWidth - ballRadius)
            ballY = min(max(ballY + yTilt * sensitivity, ballRadius), screenHeight - ballRadius)

            // Update UI
            setContent {
                BallGameScreen(
                    ballX = ballX,
                    ballY = ballY,
                    ballRadius = ballRadius,
                    onScreenSizeChange = { width, height ->
                        screenWidth = width
                        screenHeight = height
                    }
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
@Composable
fun BallGameScreen(
    ballX: Float,
    ballY: Float,
    ballRadius: Float,
    onScreenSizeChange: (Float, Float) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        onScreenSizeChange(size.width, size.height)
        drawCircle(
            color = Color.Blue,
            radius = ballRadius,
            center = Offset(ballX, ballY)
        )
        drawRect(
            color = Color.Gray,
            topLeft = Offset(300f, 300f),
            size = androidx.compose.ui.geometry.Size(200f, 50f)
        )
    }
}
