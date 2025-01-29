package com.example.galleryapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.galleryapp.data.Datasource
import com.example.galleryapp.model.GalleryImage
import com.example.galleryapp.ui.theme.GalleryAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GalleryAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GalleryApp()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GalleryListPreview() {
    GalleryAppTheme { GalleryList(imageList = Datasource().loadGalleryImages()) }
}

@Composable
fun GalleryList(imageList: List<GalleryImage>, modifier: Modifier = Modifier) {
    var activeImageId by rememberSaveable { mutableStateOf<Int?>(null) }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 130.dp),
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(imageList, key = { it.uniqueImageID })
        { imageItem ->
            Image(
                painter = painterResource(imageItem.imageResourceID),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp)
                    .heightIn(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { activeImageId = imageItem.uniqueImageID },
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
    if (activeImageId != null) {
        FullScreenImage(
            image = imageList.first {
                Log.i("Unique",""+(it.uniqueImageID))
                Log.i("Active",""+(activeImageId))
                it.uniqueImageID == activeImageId },
            onDismiss = { activeImageId = null }
        )
    }
}

@Composable
fun GalleryApp() {
    GalleryList(imageList = Datasource().loadGalleryImages())
}

@Composable
private fun FullScreenImage(
    image: GalleryImage,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scrim(onDismiss, Modifier.fillMaxSize())
        ImageWithZoom(image, Modifier.aspectRatio(1f))
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Scrim(onClose: () -> Unit, modifier: Modifier = Modifier) {
    val strClose = stringResource(R.string.close)
    Box(
        modifier
            // handle pointer input
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            // handle accessibility services
            .semantics(mergeDescendants = true) {
                contentDescription = strClose
                onClick {
                    onClose()
                    true
                }
            }
            // handle physical keyboard input
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClose()
                    true
                } else {
                    false
                }
            }
            // draw scrim
            .background(Color.DarkGray.copy(alpha = 0.75f))
    )
}


@Composable
private fun ImageWithZoom(image: GalleryImage, modifier: Modifier = Modifier) {

    var zoomed by remember { mutableStateOf(false) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    Image(
        painter = painterResource(image.imageResourceID),
        contentDescription = null,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        zoomOffset = if (zoomed) Offset.Zero else
                            calculateOffset(tapOffset, size)
                        zoomed = !zoomed
                    }
                )
            }
            .graphicsLayer {
                scaleX = if (zoomed) 2f else 1f
                scaleY = if (zoomed) 2f else 1f
                translationX = zoomOffset.x
                translationY = zoomOffset.y
            }
    )

}

private fun calculateOffset(tapOffset: Offset, size: IntSize): Offset {
    val offsetX = (-(tapOffset.x - (size.width / 2f)) * 2f)
        .coerceIn(-size.width / 2f, size.width / 2f)
    return Offset(offsetX, 0f)
}