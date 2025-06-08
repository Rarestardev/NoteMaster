package com.rarestardev.notemaster.note_editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

@Preview
@Composable
private fun BulletDotPreview() {
    NoteMasterTheme {
        AddedBulletDot(NoteEditorViewModel())
    }
}

@Composable
fun AddedBulletDot(viewModel: NoteEditorViewModel) {
    IconButton(
        onClick = {
            viewModel.textState = addBulletToSelectedLine(viewModel.textState)
        },
        modifier = Modifier.padding(6.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.icon_text_bullet_list_square),
            contentDescription = stringResource(R.string.dot_bullet_icon_desc),
            modifier = Modifier.size(dimensionResource(R.dimen.bottom_bar_note_Activity_icon_size))
        )
    }
}

private fun addBulletToSelectedLine(textFieldValue: TextFieldValue): TextFieldValue {
    val text = textFieldValue.text
    val cursorPosition = textFieldValue.selection.start

    val lines = text.lines().toMutableList()
    var currentLineIndex = 0
    var charCount = 0

    for ((index, line) in lines.withIndex()) {
        charCount += line.length + 1

        if (cursorPosition <= charCount) {
            currentLineIndex = index
            break
        }
    }

    if (!lines[currentLineIndex].startsWith("• ")) {
        lines[currentLineIndex] = "• " + lines[currentLineIndex]
    }

    return textFieldValue.copy(text = lines.joinToString("\n"))
}