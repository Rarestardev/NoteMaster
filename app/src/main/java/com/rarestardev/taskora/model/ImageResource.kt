package com.rarestardev.taskora.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
sealed class ImageResource {
    data class Vector(val vector: ImageVector) : ImageResource()
    data class Painter(val drawable: Int) : ImageResource()
}