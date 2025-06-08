package com.rarestardev.notemaster.view_model

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.rarestardev.notemaster.model.StyledSegment

/**
 * ViewModel for managing text editing and styling operations.
 *
 * @author Rarestardev
 *
 * 2025/06/08  JUN
 */
@SuppressLint("MutableCollectionMutableState", "AutoboxingStateCreation")
class NoteEditorViewModel : ViewModel() {

    /**
     * Stores the text alignment direction (start, center, end).
     */
    var directionText by mutableStateOf(TextAlign.Start)

    /**
     * Updates the text alignment.
     *
     * @param direction The new text alignment value.
     */
    fun setDirection(direction: TextAlign) {
        directionText = direction
    }

    /**
     * Stores the text size (default is 16sp).
     */
    var textSizeSliderValue by mutableStateOf(16.sp)

    /**
     * Updates the text size.
     *
     * @param size The new text size value in SP.
     */
    fun updateTextSize(size: Float) {
        textSizeSliderValue = size.sp
    }

    /**
     * Stores the selected text color.
     */
    var selectedColor by mutableStateOf(Color.Black)

    /**
     * Updates the selected text color.
     *
     * @param color The new color value.
     */
    fun updateColor(color: Color) {
        selectedColor = color
    }

    /**
     * Stores the font weight value (default is normal).
     */
    var fontWeightValue by mutableStateOf(FontWeight.Normal)

    /**
     * Updates the font weight.
     *
     * @param newFontWeight The new font weight value.
     */
    fun updateFontWeight(newFontWeight: FontWeight) {
        fontWeightValue = newFontWeight
    }

    /**
     * Stores the font style value (default is normal).
     */
    var fontStyleValue by mutableStateOf(FontStyle.Normal)

    /**
     * Updates the font style.
     *
     * @param newFontStyle The new font style value.
     */
    fun updateFontStyle(newFontStyle: FontStyle) {
        fontStyleValue = newFontStyle
    }

    /**
     * Stores the current text field value.
     */
    var textState by mutableStateOf(TextFieldValue(""))

    /**
     * Indicates whether text is selected.
     */
    var isSelection by mutableStateOf(true)

    /**
     * Stores styled segments of text.
     */
    var styledSegments by mutableStateOf(mutableListOf<StyledSegment>())

    /**
     * Handles text changes and applies existing styles.
     *
     * @param newValue The new `TextFieldValue` after a user input change.
     */
    fun onTextChanged(newValue: TextFieldValue) {
        textState = newValue
        isSelection = newValue.selection.collapsed

        textState = newValue.copy(
            annotatedString = applyStylesToText(textState, styledSegments),
            selection = textState.selection
        )
    }

    /**
     * Applies saved styles to the text.
     *
     * @param textState The current text field value.
     * @param styledSegments A list of previously saved styled segments.
     * @return AnnotatedString with styles applied.
     */
    private fun applyStylesToText(
        textState: TextFieldValue,
        styledSegments: MutableList<StyledSegment>
    ): AnnotatedString {
        val text = textState.text
        return buildAnnotatedString {
            append(text)

            styledSegments.forEach { segment ->
                if (segment.end <= text.length) {
                    addStyle(segment.style, segment.start, segment.end)
                }
            }
        }
    }

    /**
     * Saves a style for a selected text range.
     *
     * @param start The start index of the styled text.
     * @param end The end index of the styled text.
     * @param spanStyle The styling attributes to apply.
     * @param styledSegments The list where styled segments are stored.
     */
    fun saveStylesOnText(
        start: Int,
        end: Int,
        spanStyle: SpanStyle,
        styledSegments: MutableList<StyledSegment>
    ) {
        styledSegments.add(
            StyledSegment(start, end, spanStyle)
        )

        textState = textState.copy(
            annotatedString = applyStylesToText(textState, styledSegments),
            selection = TextRange(textState.text.length)
        )
    }
}