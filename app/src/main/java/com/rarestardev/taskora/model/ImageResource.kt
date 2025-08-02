package com.rarestardev.taskora.model

import androidx.compose.ui.graphics.vector.ImageVector

sealed class ImageResource {
    data class Vector(val vector: ImageVector) : ImageResource()
    data class Painter(val drawable: Int) : ImageResource()
}