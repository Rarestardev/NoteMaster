package com.rarestardev.notemaster.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.components.pager_view.NoteViewPager
import com.rarestardev.notemaster.components.pager_view.TaskViewPager
import com.rarestardev.notemaster.view_model.NoteEditorViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerView(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel,
    noteViewModel: NoteEditorViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val titlePage = listOf("High Priority Tasks", "High Priority Notes")

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
        val (titleRef, dividerRef, divider2Ref, pagerRef, indicatorRef) = createRefs()
        AnimatedContent(
            targetState = pagerState.currentPage,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            },
            label = "Page Title Animation"
        ) {
            Text(
                text = titlePage[it],
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(dividerRef) {
                    top.linkTo(titleRef.bottom, 6.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            thickness = 0.3.dp,
            color = Color.White
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(pagerRef) {
                    top.linkTo(dividerRef.bottom, 6.dp)
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

        HorizontalPagerIndicator(
            pagerState = PagerState(
                pageCount = 2,
                currentPage = pagerState.currentPage
            ),
            modifier = Modifier.constrainAs(indicatorRef) {
                bottom.linkTo(parent.bottom, 6.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            activeColor = Color.White,
            inactiveColor = colorResource(R.color.text_field_label_color),
            indicatorWidth = 4.dp
        )
    }
}