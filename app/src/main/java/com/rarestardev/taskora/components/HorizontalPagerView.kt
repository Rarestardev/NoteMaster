package com.rarestardev.taskora.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.pager_view.AdBannerPager
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
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(6000)
            val nextPage = (pagerState.currentPage + 1) % 3
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
                2 -> AdBannerPager()
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

        HorizontalPagerIndicator(
            pagerState = PagerState(
                pageCount = 3,
                currentPage = pagerState.currentPage
            ),
            modifier = Modifier.constrainAs(indicatorRef) {
                bottom.linkTo(parent.bottom, 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            activeColor = Color.White,
            inactiveColor = colorResource(R.color.text_field_label_color),
            indicatorWidth = 4.dp
        )
    }
}