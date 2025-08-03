package com.rarestardev.taskora.components.pager_view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.rarestardev.taskora.R

@Composable
fun AdBannerPager() {
    val context = LocalContext.current
    val uri = "http://cafebazaar.ir/app/?id=com.rarestardev.magneticplayer&ref=share"
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.magnetic_player), // ads
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.3.dp,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{
                    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                    context.startActivity(intent)
                }
                .height(200.dp)
                .background(Color.Blue, MaterialTheme.shapes.medium)
        ){

        }
    }
}