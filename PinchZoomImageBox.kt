package com.sarang.torang.di.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PinchZoomImageBox(
    contents: @Composable (
        @Composable (Modifier, String, Dp?, Dp?, ContentScale?, Dp?) -> Unit,
        ZoomState
    ) -> Unit
) {
    var zoomState by remember { mutableStateOf(ZoomState()) } // 줌 이미지로부터 상태를 전달받기 위한 state
    Box(Modifier.fillMaxSize())
    {
        contents(provideZoomableTorangAsyncImage({ zoomState = it }), zoomState)

        if (zoomState.isZooming.value) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.20f))
            ) {
                val offsetX = with(LocalDensity.current) {
                    zoomState.bounds.value?.left?.toDp()
                        ?: 0.dp
                }
                val offsetY = with(LocalDensity.current) {
                    zoomState.bounds.value?.top?.toDp()
                        ?: 0.dp
                }

                provideTorangAsyncImage().invoke(
                    Modifier
                        .offset(offsetX, offsetY)
                        .fillMaxWidth()
                        .height(zoomState.originHeight.value.dp)
                        .graphicsLayer {
                            scaleX = zoomState.scale.value
                            scaleY = zoomState.scale.value
                            translationX =
                                zoomState.offsetX.value
                            translationY =
                                zoomState.offsetY.value
                        },
                    zoomState.url.value,
                    30.dp,
                    30.dp,
                    ContentScale.Crop,
                    null
                )
            }
        }
    }
}
