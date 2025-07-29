package com.rarestardev.notemaster.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.enums.ThemeMode

@Composable
fun NoteMasterTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
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

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme

    StatusBarStyler(
        isDarkTheme = isDarkTheme
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = shape
    )
}

@Composable
private fun StatusBarStyler(
    isDarkTheme: Boolean
) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window ?: return
    val insetsController = WindowCompat.getInsetsController(window,view)

    SideEffect {
        insetsController.isAppearanceLightStatusBars = !isDarkTheme
    }
}