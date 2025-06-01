package com.rarestardev.notemaster.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.model.StyledSegment
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMasterTheme {
                CustomTextFieldEditor()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Preview
@Composable
fun CustomTextFieldEditor() {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var styledSegments by remember { mutableStateOf(mutableListOf<StyledSegment>()) }

    var isBoldActive by remember { mutableStateOf(false) }
    var isColorActive by remember { mutableStateOf(false) }

    var lastStyledIndex by remember { mutableIntStateOf(0) }
    var selectedColor by remember { mutableStateOf(Color.Black) }

    var isPaletteVisible by remember { mutableStateOf(false) }

    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta)
    var textSize by remember { mutableFloatStateOf(18f) }

    var isTextSelected by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(state = rememberScrollState())
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
        TextField(
            value = textState,
            onValueChange = { newText ->
                val selection = textState.selection
                if (selection.min < selection.max) {
                    styledSegments.add(
                        StyledSegment(
                            selection.min,
                            selection.max,
                            SpanStyle(fontWeight = FontWeight.Bold)
                        )
                    )
                } else {
                    Log.e("Error", "محدوده انتخاب‌شده معتبر نیست!")
                }

                isTextSelected = newText.selection.min != newText.selection.max
                if (newText.text.length > textState.text.length) {
                    lastStyledIndex = textState.text.length
                }
                textState = newText.copy(
                    annotatedString = applyStylesToText(
                        newText.text,
                        styledSegments,
                        isBoldActive,
                        isColorActive,
                        lastStyledIndex
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            textStyle = TextStyle(fontSize = textSize.sp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )

        Button(onClick = { isBoldActive = !isBoldActive }) {
            Text(if (isBoldActive) "✅ بولد فعال" else "⚪ فعال‌سازی بولد")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { isColorActive = !isColorActive }) {
            Text(if (isColorActive) "✅ رنگ فعال" else "⚪ فعال‌سازی رنگ")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            isPaletteVisible = true
        }) {
            Text("ColorPlatte")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            textState = addBulletToSelectedLine(textState, "• ")
        }) {
            Text("افزودن نقطه به ابتدای هر خط")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            textState = addBulletToSelectedLine(textState, "* ")
        }) {
            Text("افزودن ستاره به ابتدای هر خط")
        }


        DropdownMenu(
            expanded = isPaletteVisible,
            onDismissRequest = { isPaletteVisible = false }
        ) {
            colors.forEach { color ->
                DropdownMenuItem(
                    text = { Text(" ") },
                    modifier = Modifier
                        .background(color)
                        .size(40.dp),
                    onClick = {
                        selectedColor = color
                        isPaletteVisible = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "${textSize.toInt()} sp\"")

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = textSize,
            onValueChange = { newSize -> textSize = newSize },
            valueRange = 11f..36f
        )
    }
}

private fun addBulletToSelectedLine(
    textFieldValue: TextFieldValue,
    bulletValue: String
): TextFieldValue {
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

    lines[currentLineIndex] = bulletValue + lines[currentLineIndex]

    return textFieldValue.copy(text = lines.joinToString("\n"))
}

private fun applyStylesToText(
    text: String,
    styledSegments: MutableList<StyledSegment>,
    isBoldActive: Boolean,
    isColorActive: Boolean,
    lastStyledIndex: Int
): AnnotatedString {
    return buildAnnotatedString {
        append(text)

        styledSegments.forEach { segment ->
            if (segment.end <= text.length) {
                addStyle(segment.style, segment.start, segment.end)
            }
        }

        if (isBoldActive) {
            val newSegment =
                StyledSegment(lastStyledIndex, text.length, SpanStyle(fontWeight = FontWeight.Bold))
            if (lastStyledIndex in 0..text.length) {
                addStyle(newSegment.style, newSegment.start, newSegment.end)
                styledSegments.add(newSegment)
            }
        }

        if (isColorActive) {
            val newSegment =
                StyledSegment(lastStyledIndex, text.length, SpanStyle(color = Color.Blue))
            if (lastStyledIndex in 0..text.length) {
                addStyle(newSegment.style, newSegment.start, newSegment.end)
                styledSegments.add(newSegment)
            }
        }
    }
}

private fun applyStyle(
    styledSegments: MutableList<StyledSegment>,
    textFieldValue: TextFieldValue,
    style: SpanStyle
) {
    val selection = textFieldValue.selection
    if (selection.min != selection.max) {
        styledSegments.add(StyledSegment(selection.min, selection.max, style))
    }
}