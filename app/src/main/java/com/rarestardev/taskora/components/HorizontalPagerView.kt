package com.rarestardev.taskora.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.pager.ExperimentalPagerApi
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.pager_view.NoteViewPager
import com.rarestardev.taskora.components.pager_view.TaskViewPager
import com.rarestardev.taskora.view_model.NoteEditorViewModel
import com.rarestardev.taskora.view_model.TaskViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerView(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel,
    noteViewModel: NoteEditorViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(6000)
            val nextPage = (pagerState.currentPage + 1) % 2
            pagerState.animateScrollToPage(nextPage)
        }
    }

    ConstraintLayout(
        modifier = modifier.padding(8.dp)
    ) {
        val (divider2Ref, pagerRef, indicatorRef) = createRefs()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(pagerRef) {
                    top.linkTo(parent.top, 6.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(divider2Ref.top, 6.dp)
                    height = Dimension.fillToConstraints
                }
        ) { pages ->
            when (pages) {
                0 -> TaskViewPager(taskViewModel)
                1 -> NoteViewPager(noteViewModel)
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(divider2Ref) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(indicatorRef.top, 8.dp)
                },
            thickness = 0.3.dp,
            color = Color.White
        )

        HorizontalPagerIndicators(
            modifier = Modifier.constrainAs(indicatorRef) {
                bottom.linkTo(parent.bottom, 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            currentPage = pagerState.currentPage
        )
    }
}

@Composable
private fun HorizontalPagerIndicators(
    modifier: Modifier = Modifier,
    currentPage: Int
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(2) { i ->
                val color = if (currentPage == i) {
                    Color.White
                } else {
                    colorResource(R.color.text_field_label_color)
                }

                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}