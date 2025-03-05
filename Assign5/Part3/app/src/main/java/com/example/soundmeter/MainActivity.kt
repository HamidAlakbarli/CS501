package com.example.soundmeter

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.log10

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request microphone permission
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Microphone permission required!", Toast.LENGTH_LONG).show()
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            SoundMeterScreen()
        }
    }
}

@Composable
fun SoundMeterScreen() {
    val context = LocalContext.current
    val noiseLevel = remember { mutableStateOf(0f) }
    val threshold = 70f  // Alert if noise > 70 dB

    LaunchedEffect(Unit) {
        val audioRecord = createAudioRecorder()
        val buffer = ShortArray(1024)
        audioRecord.startRecording()

        while (true) {
            val readSize = audioRecord.read(buffer, 0, buffer.size)
            if (readSize > 0) {
                val amplitude = buffer.map { it.toInt().absoluteValue }.average().toFloat()
                val decibels = if (amplitude > 0) 20 * log10(amplitude) else 0f
                noiseLevel.value = decibels
                Log.d("SoundMeter", "Decibels: $decibels dB")
            }
            delay(200) 
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sound Level: ${noiseLevel.value.toInt()} dB", fontSize = 24.sp)

        // Visual Sound Meter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(if (noiseLevel.value > threshold) Color.Red else Color.Green, shape = RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(noiseLevel.value / 100f)
                    .height(30.dp)
                    .background(Color.Yellow, shape = RoundedCornerShape(10.dp))
            )
        }

        if (noiseLevel.value > threshold) {
            Text("⚠️ High Noise Level!", color = Color.Red, fontSize = 20.sp)
        }
    }
}

// Helper function to create an AudioRecord instance
fun createAudioRecorder(): AudioRecord {
    val sampleRate = 44100
    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    return AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )
}
