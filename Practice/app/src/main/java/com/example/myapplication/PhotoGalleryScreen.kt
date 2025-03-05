package com.example.myapplication

import android.content.Context
import android.util.Xml
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser

@Composable
fun PhotoGalleryScreen(snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val photos = remember { loadPhotosFromXml(context) }
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photo ->
            PhotoItemView(photo) {
                scope.launch { snackbarHostState.showSnackbar("You clicked: ${photo.title}") }
            }
        }
    }
}

@Composable
fun PhotoItemView(photo: PhotoItem, onClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isExpanded) 1.5f else 1f, label = "scale")

    val context = LocalContext.current
    val imageResId = remember(photo.imageName) {
        context.resources.getIdentifier(photo.imageName, "drawable", context.packageName)
    }

    val validImageResId = if (imageResId != 0) imageResId else android.R.color.darker_gray

    Image(
        painter = painterResource(id = validImageResId),
        contentDescription = photo.title,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isExpanded) 220.dp else 120.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable {
                isExpanded = !isExpanded
                onClick()
            },
        contentScale = ContentScale.Crop
    )
}

data class PhotoItem(
    val imageName: String,
    val title: String
)

fun loadPhotosFromXml(context: Context): List<PhotoItem> {
    val photos = mutableListOf<PhotoItem>()
    val parser: XmlPullParser = context.resources.openRawResource(R.raw.photos).let { inputStream ->
        Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
    }

    var eventType = parser.eventType
    var imageName: String? = null
    var title: String? = null

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                if (parser.name == "photo") {
                    imageName = parser.getAttributeValue(null, "imageName")
                    title = parser.getAttributeValue(null, "title")
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "photo" && imageName != null && title != null) {
                    photos.add(PhotoItem(imageName, title))
                    imageName = null
                    title = null
                }
            }
        }
        eventType = parser.next()
    }

    return photos
}
