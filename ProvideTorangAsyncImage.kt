package com.sarang.torang.di.image

import TorangAsyncImage


fun provideTorangAsyncImage(): TorangAsyncImageType = {
    TorangAsyncImage(
        modifier = it.modifier,
        model = it.model,
        progressSize = it.progressSize,
        errorIconSize = it.errorIconSize,
        contentScale = it.contentScale
    )
}