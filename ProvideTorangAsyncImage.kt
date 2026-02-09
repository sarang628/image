package com.sarang.torang.di.image

import TorangAsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sarang.torang.di.pinchzoom.PinchZoomState
import com.sarang.torang.di.pinchzoom.ZoomSnapshot
import com.sarang.torang.di.pinchzoom.isZooming
import com.sarang.torang.di.pinchzoom.pinchZoomAndTransform
import kotlinx.coroutines.flow.distinctUntilChanged


typealias TorangAsyncImageType = @Composable (TorangAsyncImageData) -> Unit

typealias ZoomableTorangAsyncImage = @Composable (
    ImageLoadData
) -> Unit

data class TorangAsyncImageData(
    val modifier            : Modifier      = Modifier,
    val model               : Any?          = "",
    val progressSize        : Dp            = 50.dp,
    val errorIconSize       : Dp            = 50.dp,
    val contentScale        : ContentScale  = ContentScale.Fit,
    val contentDescription  : String?       = null,
)

fun provideTorangAsyncImage(): TorangAsyncImageType = {
        TorangAsyncImage(
            modifier = it.modifier,
            model = it.model,
            progressSize = it.progressSize,
            errorIconSize = it.errorIconSize,
            contentScale = it.contentScale
        )
    }

fun provideZoomableTorangAsyncImage(onZoomState: (PinchZoomState) -> Unit = {}): ZoomableTorangAsyncImage =
    { data ->
        val zoomState =
            remember {
                PinchZoomState(
                    originHeight = data.height ?: 0.dp,
                    url = data.url ?: ""
                )
            }

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

data class ImageLoadData(
    val modifier: Modifier = Modifier,
    val url: String? = null,
    val progressSize: Dp = 50.dp,
    val errorIconSize: Dp = 50.dp,
    val contentScale: ContentScale = ContentScale.Fit,
    val height: Dp? = null
)