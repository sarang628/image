package com.sarang.torang.di.image

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ImageLoadData(
    val modifier: Modifier = Modifier,
    val url: String? = null,
    val progressSize: Dp = 50.dp,
    val errorIconSize: Dp = 50.dp,
    val contentScale: ContentScale = ContentScale.Fit,
    val height: Dp? = null
)