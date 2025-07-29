package com.rarestardev.notemaster.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.utilities.Constants

@Composable
fun BannerAds() {
    if (Constants.ADS_VIEW) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.onSecondary)
        )
    }
}