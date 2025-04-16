package com.sarang.torang.di.image

import TorangAsyncImage
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sarang.torang.compose.feed.ZoomSnapshot
import kotlinx.coroutines.flow.distinctUntilChanged

fun provideTorangAsyncImage(): @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit =
    { modifier, model, progressSize, errorIconSize, contentScale ->
        TorangAsyncImage(
            modifier = modifier,
            model = model,
            progressSize = progressSize ?: 50.dp,
            errorIconSize = errorIconSize ?: 50.dp,
            contentScale = contentScale ?: ContentScale.Fit
        )
    }

fun provideZoomableTorangAsyncImage(onZoomState: (ZoomState) -> Unit = {}): @Composable (Modifier, String, Dp?, Dp?, ContentScale?, Dp?) -> Unit =
    { modifier, model, progressSize, errorIconSize, contentScale, height ->
        val zoomState = remember { ZoomState() }

        LaunchedEffect(zoomState) {
            snapshotFlow {
                ZoomSnapshot(
                    zoomState.scale.value,
                    zoomState.offsetX.value,
                    zoomState.offsetY.value,
                    zoomState.isZooming.value,
                    zoomState.url.value
                )
            }
                .distinctUntilChanged()
                .collect {
                    onZoomState(zoomState)
                }
        }

        TorangAsyncImage(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            zoomState.url.value = model
                            //onPressed.invoke()
                            tryAwaitRelease()
                            //onReleased.invoke()
                        }
                    )
                }
                .pinchZoomOverlay(zoomState)
                .onGloballyPositioned { coordinates ->
                    val bounds = coordinates.boundsInWindow()
                    zoomState.bounds.value = bounds
                }
                .graphicsLayer {
                    scaleX = zoomState.scale.value
                    scaleY = zoomState.scale.value
                    translationX = zoomState.offsetX.value
                    translationY = zoomState.offsetY.value
                },
            model = model,
            progressSize = progressSize ?: 50.dp,
            errorIconSize = errorIconSize ?: 50.dp,
            contentScale = contentScale ?: ContentScale.Fit
        )
    }