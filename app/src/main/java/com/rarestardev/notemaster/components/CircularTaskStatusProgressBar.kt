package com.rarestardev.notemaster.components

import com.rarestardev.notemaster.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme

@Preview
@Composable
private fun PreviewProgressBar() {
    NoteMasterTheme {
        CircularTaskStatusBar(
            10,
            3
        )
    }
}

@Composable
fun CircularTaskStatusBar(
    totalTask: Int,
    complete: Int,
    modifier: Modifier = Modifier
) {
    val progressPercent = (complete / totalTask.toFloat()) * 100
    val sweepAngle = 360f * (complete / totalTask.toFloat())
    val progressBackground = colorResource(R.color.text_field_label_color)
    val progressColor = MaterialTheme.colorScheme.onSecondary

    val textColor = when (progressPercent.toInt()) {
        in 0..25 -> Color.Red
        in 26..50 -> Color(0xFFFF3800) // orange
        in 51..75 -> Color(0xFFFFEB3B) // yellow
        in 76..99 -> Color(0xFF2196F3) // blue
        100 -> Color(0xFF4CAF50) // green
        else -> Color.Gray
    }

    Box(
        contentAlignment = Alignment.Center, modifier = modifier.size(50.dp)
    ) {
        Canvas(
            modifier = modifier.fillMaxSize()
        ) {
            var startAngle = -90f

            drawArc( // gray
                color = progressBackground,
                startAngle = startAngle,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 30f, cap = StrokeCap.Round)
            )

            drawArc(
                color = progressColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 30f, cap = StrokeCap.Round)
            )

        }

        Text(
            text = "${progressPercent.toInt()}%",
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}