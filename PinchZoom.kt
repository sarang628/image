package com.sarang.torang.di.image


import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

data class ZoomState(
    val bounds: MutableState<Rect?> = mutableStateOf(null),
    val scale: MutableState<Float> = mutableFloatStateOf(1f),
    val offsetX: MutableState<Float> = mutableFloatStateOf(0f),
    val offsetY: MutableState<Float> = mutableFloatStateOf(0f),
    val isZooming: MutableState<Boolean> = mutableStateOf(false),
    val url: MutableState<String> = mutableStateOf("")
)

@Stable
@Composable
fun Modifier.pinchZoom(onPinchZoom: (Boolean) -> Unit): Modifier {
    val scale = remember { mutableFloatStateOf(1f) }
    val offsetX = remember { mutableFloatStateOf(1f) }
    val offsetY = remember { mutableFloatStateOf(1f) }
    val plantImageZIndex = remember { mutableFloatStateOf(1f) }
    val maxScale = remember { mutableFloatStateOf(1f) }
    val minScale = remember { mutableFloatStateOf(3f) }
    return this
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown()
                do {
                    val event = awaitPointerEvent()
                    scale.value *= event.calculateZoom()
                    if (scale.value > 1) {
                        onPinchZoom.invoke(true)
                        plantImageZIndex.value = 5f
                        val offset = event.calculatePan()
                        offsetX.value += offset.x
                        offsetY.value += offset.y
                    } else {
                        onPinchZoom.invoke(false)
                    }
                } while (event.changes.any { it.pressed })
                if (currentEvent.type == PointerEventType.Release) {
                    scale.value = 1f
                    offsetX.value = 1f
                    offsetY.value = 1f
                    plantImageZIndex.value = 1f
                }
            }
        }
        .graphicsLayer {
            scaleX = maxOf(maxScale.value, minOf(minScale.value, scale.value))
            scaleY = maxOf(maxScale.value, minOf(minScale.value, scale.value))
            translationX = offsetX.value
            translationY = offsetY.value
        }
}

fun Modifier.pinchZoomOverlay(
    zoomState: ZoomState
): Modifier = composed {
    pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown()
            do {
                val event = awaitPointerEvent()
                val zoom = event.calculateZoom()
                val pan = event.calculatePan()

                zoomState.scale.value *= zoom
                if (zoomState.scale.value > 1f) {
                    zoomState.isZooming.value = true
                    zoomState.offsetX.value += pan.x
                    zoomState.offsetY.value += pan.y
                } else {
                    zoomState.isZooming.value = false
                }
            } while (event.changes.any { it.pressed })

            // 터치 끝나면 리셋
            zoomState.scale.value = 1f
            zoomState.offsetX.value = 0f
            zoomState.offsetY.value = 0f
            zoomState.isZooming.value = false
        }
    }
}