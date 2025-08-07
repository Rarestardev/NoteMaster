package com.rarestardev.taskora.feature

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun CustomText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 14.sp,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    textAlign: TextAlign = TextAlign.Start,
    fontFamily: FontFamily = FontFamily.Default,
    minLines : Int = 1,
    maxLines : Int = 1
) {
    val lang = Locale.getDefault().language

    if (lang == "fa") {
        Text(
            text = text.toPersianDigit(),
            modifier = modifier,
            style = style,
            fontWeight = fontWeight,
            fontSize = fontSize,
            color = color,
            textAlign = textAlign,
            fontFamily = fontFamily,
            minLines = minLines,
            maxLines = maxLines
        )
    } else if (lang == "en") {
        Text(
            text = text,
            modifier = modifier,
            style = style,
            fontWeight = fontWeight,
            fontSize = fontSize,
            color = color,
            textAlign = textAlign,
            fontFamily = fontFamily,
            minLines = minLines,
            maxLines = maxLines
        )
    }
}

private fun String.toPersianDigit(): String {
    val enDigit = '0'..'9'
    val faDigit = listOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    return this.map { char ->
        if (char in enDigit) {
            faDigit[char.toString().toInt()]
        } else {
            char
        }
    }.joinToString("")
}
