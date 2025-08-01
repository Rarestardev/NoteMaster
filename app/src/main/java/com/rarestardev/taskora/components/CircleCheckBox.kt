package com.rarestardev.taskora.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.ui.theme.NoteMasterTheme

@Preview
@Composable
private fun CircleCheckBoxPreview() {
    NoteMasterTheme {
        CircleCheckBox(true) { }
    }
}

@Composable
fun CircleCheckBox(
    checked : Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange : (Boolean) -> Unit
){
    val borderColor = colorResource(R.color.priority_low)
    val tickColor = MaterialTheme.colorScheme.onSecondary
    val shape = CircleShape
    val size = 24.dp
    val borderWidth = 1.dp

    Box (
        modifier = modifier
            .padding(6.dp)
            .size(size)
            .clip(shape)
            .border(borderWidth,borderColor,shape)
            .clickable{
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ){
        if (checked){
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.padding(3.dp),
                tint = tickColor
            )
        }
    }
}