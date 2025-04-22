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
import com.sarang.torang.di.pinchzoom.ImageLoader
import com.sarang.torang.di.pinchzoom.PinchZoomState
import com.sarang.torang.di.pinchzoom.ZoomSnapshot
import com.sarang.torang.di.pinchzoom.isZooming
import com.sarang.torang.di.pinchzoom.pinchZoomAndTransform
import kotlinx.coroutines.flow.distinctUntilChanged


typealias TorangAsyncImageType = @Composable (
    modifier: Modifier,
    text: String,
    width: Dp?,
    height: Dp?,
    contentScale: ContentScale?
) -> Unit

typealias ZoomableTorangAsyncImage = @Composable (
    modifier: Modifier,
    text: String,
    width: Dp?,
    height: Dp?,
    contentScale: ContentScale?,
    originHeight: Dp?
) -> Unit

fun provideTorangAsyncImage(): TorangAsyncImageType =
    { modifier, model, progressSize, errorIconSize, contentScale ->
        TorangAsyncImage(
            modifier = modifier,
            model = model,
            progressSize = progressSize ?: 50.dp,
            errorIconSize = errorIconSize ?: 50.dp,
            contentScale = contentScale ?: ContentScale.Fit
        )
    }

fun provideZoomableTorangAsyncImage(onZoomState: (PinchZoomState) -> Unit = {}): ZoomableTorangAsyncImage =
    { modifier, model, progressSize, errorIconSize, contentScale, originHeight ->
        val zoomState =
            remember {
                PinchZoomState(
                    originHeight = originHeight?.value ?: 0f,
                    url = model
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
            modifier = modifier.pinchZoomAndTransform(zoomState),
            model = model,
            progressSize = progressSize ?: 50.dp,
            errorIconSize = errorIconSize ?: 50.dp,
            contentScale = contentScale ?: ContentScale.Crop
        )
    }


/**
 * 핀치줌 라이브러리 제공용 이미지로더
 */
fun provideImageLoader(): ImageLoader =
    { modifier: Modifier,
      url: String,
      contentScale: ContentScale? ->
        provideTorangAsyncImage().invoke(modifier, url, 30.dp, 30.dp, contentScale)
    }