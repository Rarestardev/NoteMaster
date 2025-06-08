package com.rarestardev.notemaster.note_editor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

@Preview
@Composable
private fun FontStyleDropMenuPreview() {
    NoteMasterTheme {
        FontStyleDropMenu(NoteEditorViewModel())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontStyleDropMenu(viewModel: NoteEditorViewModel) {
    var isShowFontStyleMenu by remember { mutableStateOf(false) }
    val fonts = listOf(
        "Normal" to FontStyle.Normal,
        "Italic" to FontStyle.Italic
    )

    ExposedDropdownMenuBox(
        expanded = isShowFontStyleMenu,
        onExpandedChange = { isShowFontStyleMenu = !isShowFontStyleMenu }
    ) {
        IconButton(
            onClick = { isShowFontStyleMenu = true },
            modifier = Modifier
                .padding(6.dp)
                .menuAnchor(MenuAnchorType.SecondaryEditable, true)
        ) {
            Image(
                painter = painterResource(R.drawable.icons_font_style_formatting),
                contentDescription = stringResource(R.string.font_style_icon_desc),
                modifier = Modifier.size(dimensionResource(R.dimen.bottom_bar_note_Activity_icon_size))
            )
        }

        DropdownMenu(
            expanded = isShowFontStyleMenu,
            onDismissRequest = { isShowFontStyleMenu = false },
            modifier = Modifier
                .border(0.3.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(8.dp))
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    RoundedCornerShape(8.dp)
                )
        ) {
            fonts.forEach { style ->
                DropdownMenuItem(
                    text = {
                        DropMenuText(style.first, style.second)
                    },
                    onClick = {
                        if (!viewModel.isSelection) { // text is selected
                            val selectionStart = viewModel.textState.selection.start
                            val selectionEnd = viewModel.textState.selection.end
                            if (selectionStart < selectionEnd) {
                                viewModel.saveStylesOnText(
                                    selectionStart,
                                    selectionEnd,
                                    spanStyle = SpanStyle(fontStyle = style.second),
                                    viewModel.styledSegments
                                )
                            } else Log.e(
                                Constants.APP_LOG,
                                "Reversed range is not supported (Font Style Drop Menu)"
                            )
                        } else {
                            if (viewModel.textState.text.isEmpty()) {
                                viewModel.updateFontStyle(style.second)
                            } else {
                                viewModel.saveStylesOnText(
                                    0,
                                    viewModel.textState.text.length,
                                    spanStyle = SpanStyle(fontStyle = style.second),
                                    viewModel.styledSegments
                                )
                                viewModel.updateFontStyle(style.second)
                            }
                        }
                        isShowFontStyleMenu = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DropMenuText(name: String, fontStyle: FontStyle) {
    Text(
        text = name,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        fontStyle = fontStyle,
        modifier = Modifier.padding(4.dp)
    )
}