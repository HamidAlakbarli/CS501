package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyScreen()
        }
    }
}

@Composable
fun MyScreen() {
    val message = remember { mutableStateOf("") } // State to hold the text

    Column {
        Text(text = message.value) // Display the message
        Button(onClick = { message.value = "Hello World!" }) {
            Text("Click Me") // Button text
        }
    }
}

@Preview
@Composable
fun MyScreenPreview() {
    MyScreen()
}
