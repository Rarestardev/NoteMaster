package com.rarestardev.notemaster.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.rarestardev.notemaster.R

@Composable
fun NoteMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColorScheme = lightColorScheme(
        background = colorResource(R.color.background_light_mode),
        secondary = colorResource(R.color.second_color_any_mode),
        onPrimary = colorResource(R.color.text_light_mode),
        onSecondary = colorResource(R.color.second_color_2),
        onSecondaryContainer = colorResource(R.color.second_light_mode)
    )

    val darkColorScheme = darkColorScheme(
        background = colorResource(R.color.background_night_mode),
        secondary = colorResource(R.color.second_color_any_mode),
        onPrimary = colorResource(R.color.text_night_mode),
        onSecondary = colorResource(R.color.second_color_2),
        onSecondaryContainer = colorResource(R.color.second_night_mode)
    )

    val colorScheme = when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = shape
    )
}