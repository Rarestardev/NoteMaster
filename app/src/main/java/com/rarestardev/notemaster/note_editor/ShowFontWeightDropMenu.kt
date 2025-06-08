package com.rarestardev.notemaster.note_editor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
private fun FontWeightDropMenuPreview() {
    NoteMasterTheme {
        FontWeightDropMenu(NoteEditorViewModel())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontWeightDropMenu(viewModel: NoteEditorViewModel) {
    var isShowListOfFontWeight by remember { mutableStateOf(false) }
    val fonts = listOf(
        "SemiBold" to FontWeight.SemiBold,
        "Bold" to FontWeight.Bold,
        "ExtraBold" to FontWeight.ExtraBold,
        "Normal" to FontWeight.Normal,
        "Medium" to FontWeight.Medium,
        "Light" to FontWeight.Light,
        "ExtraLight" to FontWeight.ExtraLight
    )

    ExposedDropdownMenuBox(
        expanded = isShowListOfFontWeight,
        onExpandedChange = { isShowListOfFontWeight = !isShowListOfFontWeight }
    ) {
        DropdownMenu(
            expanded = isShowListOfFontWeight,
            onDismissRequest = { isShowListOfFontWeight = false }
        ) {
            fonts.forEach { weight ->
                DropdownMenuItem(
                    text = { DropMenuText(weight.first, weight.second) },
                    onClick = {
                        if (!viewModel.isSelection) { // text is selected
                            val selectionStart = viewModel.textState.selection.start
                            val selectionEnd = viewModel.textState.selection.end
                            if (selectionStart < selectionEnd) {
                                viewModel.saveStylesOnText(
                                    selectionStart,
                                    selectionEnd,
                                    spanStyle = SpanStyle(fontWeight = weight.second),
                                    viewModel.styledSegments
                                )
                            } else Log.e(
                                Constants.APP_LOG,
                                "Reversed range is not supported (Font weight drop Menu)"
                            )
                        } else {
                            if (viewModel.textState.text.isEmpty()) {
                                viewModel.updateFontWeight(weight.second)
                            } else {
                                viewModel.saveStylesOnText(
                                    0,
                                    viewModel.textState.text.length,
                                    spanStyle = SpanStyle(fontWeight = weight.second),
                                    viewModel.styledSegments
                                )
                                viewModel.updateFontWeight(weight.second)
                            }
                        }
                        isShowListOfFontWeight = false
                    }
                )
            }
        }

        IconButton(
            onClick = { isShowListOfFontWeight = true },
            modifier = Modifier
                .padding(6.dp)
                .menuAnchor(MenuAnchorType.SecondaryEditable, true)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_bold),
                contentDescription = stringResource(R.string.font_weight_icon_desc),
                modifier = Modifier
                    .size(dimensionResource(R.dimen.bottom_bar_note_Activity_icon_size))
                    .padding(2.dp)
            )
        }
    }
}

@Composable
private fun DropMenuText(name: String, fontWeight: FontWeight) {
    Text(
        text = name,
        fontWeight = fontWeight,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(4.dp)
    )
}