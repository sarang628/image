package com.sarang.torang.di.image

import TorangAsyncImage
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import com.sarang.torang.di.pinchzoom.PinchZoomState
import com.sarang.torang.di.pinchzoom.ZoomSnapshot
import com.sarang.torang.di.pinchzoom.isZooming
import com.sarang.torang.di.pinchzoom.pinchZoomAndTransform
import kotlinx.coroutines.flow.distinctUntilChanged

fun provideZoomableTorangAsyncImage(onZoomState: (PinchZoomState) -> Unit = {}): ZoomableTorangAsyncImage =
{ data ->
    val zoomState =
        remember { PinchZoomState(originHeight  = data.height ?: 0.dp,
                                  url           = data.url ?: "") }

    LaunchedEffect(zoomState) {
        snapshotFlow {
            ZoomSnapshot(
                zoomState.accumulateZoom.value,
                zoomState.offset.value,
                zoomState.isZooming
            )
        }.distinctUntilChanged()
            .collect {
                onZoomState(zoomState)
            }
    }

    TorangAsyncImage(
        modifier = data.modifier.pinchZoomAndTransform(zoomState),
        model = data.url ?: "",
        progressSize = data.progressSize,
        errorIconSize = data.errorIconSize,
        contentScale = data.contentScale
    )
}