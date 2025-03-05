package com.example.myapplication

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContentScreen()
        }
    }
}

@Composable
fun ContentScreen() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            ContentList(modifier = Modifier.weight(1f))
            ContentDetail(modifier = Modifier.weight(1f))
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            ContentList(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ContentList(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(8.dp)) {
        items(10) { index ->
            ContentItem(title = "Content Title $index", description = "This is a short description of the content item $index.")
        }
    }
}

@Composable
fun ContentItem(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Text(text = description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ContentDetail(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Select an item to view details", fontSize = 18.sp, color = Color.Black)
    }
}
