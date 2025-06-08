package com.rarestardev.notemaster.note_editor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

@Preview
@Composable
private fun ColorPalettePreview() {
    NoteMasterTheme {
        ColorPaletteDropMenu(NoteEditorViewModel())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPaletteDropMenu(viewModel: NoteEditorViewModel) {
    var isPaletteVisible by remember { mutableStateOf(false) }
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Gray)

    val menuItemWidth by remember { mutableIntStateOf((colors.size / 2) * 40) }

    ExposedDropdownMenuBox(
        expanded = isPaletteVisible,
        onExpandedChange = { isPaletteVisible = !isPaletteVisible }
    ) {
        IconButton(
            onClick = { isPaletteVisible = true },
            modifier = Modifier
                .padding(6.dp)
                .menuAnchor(MenuAnchorType.SecondaryEditable, true)
        ) {
            Image(
                painter = painterResource(R.drawable.icons_rgb_color_wheel),
                contentDescription = stringResource(R.string.select_color_icon_desc),
                modifier = Modifier.size(dimensionResource(R.dimen.bottom_bar_note_Activity_icon_size))
            )
        }

        DropdownMenu(
            expanded = isPaletteVisible,
            onDismissRequest = { isPaletteVisible = false },
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .border(0.3.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(8.dp))
                .width(menuItemWidth.dp)
                .padding(1.dp)
        ) {

            val defaultColor = MaterialTheme.colorScheme.onPrimary
            if (viewModel.selectedColor != defaultColor) {
                Text(
                    "Default",
                    modifier = Modifier
                        .clickable {
                            viewModel.updateColor(defaultColor)
                        }
                        .padding(6.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                state = rememberLazyGridState(),
                modifier = Modifier
                    .width(menuItemWidth.dp)
                    .height(80.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                items(colors) { color ->
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(40.dp)
                            .background(color)
                            .clickable {
                                if (!viewModel.isSelection) { // text is selected
                                    val selectionStart = viewModel.textState.selection.start
                                    val selectionEnd = viewModel.textState.selection.end
                                    if (selectionStart < selectionEnd) {
                                        viewModel.saveStylesOnText(
                                            selectionStart,
                                            selectionEnd,
                                            spanStyle = SpanStyle(color = color),
                                            viewModel.styledSegments
                                        )
                                    } else Log.e(
                                        Constants.APP_LOG,
                                        "Reversed range is not supported (Color palette drop menu)"
                                    )
                                } else {
                                    if (viewModel.textState.text.isEmpty()) {
                                        viewModel.updateColor(color)
                                    } else {
                                        viewModel.saveStylesOnText(
                                            0,
                                            viewModel.textState.text.length,
                                            spanStyle = SpanStyle(color = color),
                                            viewModel.styledSegments
                                        )
                                        viewModel.updateColor(color)
                                    }
                                }
                                isPaletteVisible = false
                            }
                    )
                }
            }
        }
    }
}
