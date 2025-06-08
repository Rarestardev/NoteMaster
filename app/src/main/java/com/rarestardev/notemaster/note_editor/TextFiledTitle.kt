package com.rarestardev.notemaster.note_editor

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.designs.ShowPopupMenuPriority

@Composable
private fun textFiledTitle(modifier: Modifier): String {
    var textFieldTitle by remember { mutableStateOf("") }
    val transparentColor = Color.Transparent

    TextField(
        value = textFieldTitle,
        onValueChange = { textFieldTitle = it },
        label = { LabelText(stringResource(R.string.note_title)) },
        modifier = modifier,
        leadingIcon = {
            Image(painter = painterResource(R.drawable.icons_title), "NoteTitleIcon")
        },
        trailingIcon = { ShowPopupMenuPriority() },
        minLines = 1,
        maxLines = 2,
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = transparentColor,
            focusedContainerColor = transparentColor,
            disabledContainerColor = transparentColor,
            cursorColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedIndicatorColor = transparentColor,
            focusedIndicatorColor = transparentColor
        )
    )

    return textFieldTitle
}

@Composable
private fun LabelText(title: String) {
    Text(
        text = title,
        color = colorResource(R.color.text_field_label_color),
        style = TextStyle.Default.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textDirection = TextDirection.Content
        )
    )
}