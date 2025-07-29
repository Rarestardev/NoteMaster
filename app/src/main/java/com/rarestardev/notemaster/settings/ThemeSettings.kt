package com.rarestardev.notemaster.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.enums.ThemeMode

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ThemeView(themeMode: ThemeMode, onModeChange: (ThemeMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        ThemeMode.entries.forEach { mode ->
            val isSelected = mode == themeMode

            val modifiers = Modifier
                .fillMaxWidth()
                .clickable { onModeChange(mode) }

            if (mode == ThemeMode.SYSTEM) {
                Box(
                    modifier = modifiers
                        .height(50.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    colorResource(R.color.background_light_mode),
                                    colorResource(R.color.background_night_mode)
                                )
                            ),
                            shape = MaterialTheme.shapes.small
                        )
                        .border(
                            0.3.dp,
                            color = MaterialTheme.colorScheme.onSecondary,
                            MaterialTheme.shapes.small
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, end = 20.dp)
                    ) {
                        Text(
                            text = mode.name + " " + stringResource(R.string.default_system),
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected)
                            Icon(Icons.Default.Done, mode.name, tint = Color.Green)
                    }
                }
            } else {
                val background = when (mode) {
                    ThemeMode.LIGHT -> colorResource(R.color.background_light_mode)
                    ThemeMode.DARK -> colorResource(R.color.background_night_mode)
                    else -> Color.Transparent
                }

                Button(
                    onClick = { onModeChange(mode) },
                    colors = ButtonDefaults.buttonColors(containerColor = background),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.height(50.dp),
                    border = BorderStroke(
                        0.3.dp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = mode.name,
                            color = if (mode == ThemeMode.DARK) Color.White else Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected)
                            Icon(Icons.Default.Done, mode.name, tint = Color.Green)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}