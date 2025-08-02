package com.rarestardev.taskora.feature

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.view_model.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(viewModel: TaskViewModel){
    val categories = stringArrayResource(id = R.array.task_categories)
    var expanded by remember { mutableStateOf(false) }
    val transparentColor = Color.Transparent
    if (viewModel.selectedCategory.isEmpty()){ viewModel.updateCategoryList(stringArrayResource(R.array.task_categories)[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = stringResource(R.string.category) + " ( " + viewModel.selectedCategory + " )",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .menuAnchor(MenuAnchorType.SecondaryEditable, true),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedIndicatorColor = transparentColor,
                focusedIndicatorColor = transparentColor,
                focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                focusedLeadingIconColor = transparentColor,
                unfocusedLeadingIconColor = transparentColor
            ),
            textStyle = MaterialTheme.typography.labelMedium,
            shape = MaterialTheme.shapes.medium,
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            maxLines = 1
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    },
                    onClick = {
                        viewModel.updateCategoryList(category)
                        expanded = false
                    }
                )
            }
        }
    }
}