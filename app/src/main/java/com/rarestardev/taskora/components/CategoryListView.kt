package com.rarestardev.taskora.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.ShowAllTasksActivity
import com.rarestardev.taskora.ui.theme.NoteMasterTheme

@Preview
@Composable
private fun CategoryPreview() {
    NoteMasterTheme {
        CategoryListView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListView() {
    val categories = stringArrayResource(id = R.array.task_categories).sortedBy { it }
    val lazyState = rememberLazyListState()
    val context = LocalContext.current
    var isShowBottomSheetCategory by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.category),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 12.dp)
        )

        Text(
            text = stringResource(R.string.see_more),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.text_field_label_color),
            modifier = Modifier
                .clickable {
                    isShowBottomSheetCategory = true
                }
                .padding(end = 12.dp)
        )

        if (isShowBottomSheetCategory) {
            ModalBottomSheet(
                onDismissRequest = { isShowBottomSheetCategory = false },
                sheetState = bottomSheetState,
                scrimColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .padding(start = 8.dp, end = 8.dp)
                                .clickable {
                                    val intent = Intent(context, ShowAllTasksActivity::class.java).apply {
                                        putExtra("Category", category)
                                    }
                                    context.startActivity(intent)
                                }
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.shapes.small
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.padding(start = 6.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )

                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp),
        state = lazyState
    ) {
        items(categories.take(10)) { category ->
            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.shapes.small
                    )
                    .border(
                        0.4.dp,
                        MaterialTheme.colorScheme.onSecondary,
                        MaterialTheme.shapes.small
                    )
                    .clickable {
                        val intent = Intent(context, ShowAllTasksActivity::class.java).apply {
                            putExtra("Category", category)
                        }
                        context.startActivity(intent)
                    }
            ) {
                Text(
                    text = category,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}