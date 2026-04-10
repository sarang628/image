package com.sarang.torang.di.image

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TorangAsyncImageData(
    val modifier            : Modifier      = Modifier,
    val model               : Any?          = "",
    val progressSize        : Dp            = 50.dp,
    val errorIconSize       : Dp            = 50.dp,
    val contentScale        : ContentScale  = ContentScale.Fit,
    val contentDescription  : String?       = null,
)