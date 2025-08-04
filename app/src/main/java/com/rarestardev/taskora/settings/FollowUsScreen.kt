package com.rarestardev.taskora.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import com.rarestardev.taskora.ui.theme.NoteMasterTheme

@Preview
@Composable
private fun FollowUsScreenPreview() {
    NoteMasterTheme {

    }
}

private data class SocialItems(
    val content: String,
    val uri: String
)

@Composable
fun FollowUsScreen() {
    val socialList = listOf(
        SocialItems(
            "Instagram",
            "https://www.instagram.com/rarestar.dev?igsh=MWlxdHdmYWE3MXpybA=="
        ),
        SocialItems(
            "LinkedIn",
            "https://www.linkedin.com/in/soheyl-darzi-707238274?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app"
        ),
        SocialItems("Github", "https://github.com/Rarestardev"),
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        socialList.forEach { list ->
            Items(
                content = list.content,
                uri = list.uri
            )
        }
    }
}

@Composable
private fun Items( content: String, uri: String) {
    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(6.dp)
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.small)
            .clickable {
                if (uri.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                    context.startActivity(intent)
                }
            }
    ) {
        val (textRef, forwardRef) = createRefs()

        Text(
            text = content,
            modifier = Modifier.constrainAs(textRef) {
                start.linkTo(parent.start, 12.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(forwardRef.start, 12.dp)
                width = Dimension.fillToConstraints
            },
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            maxLines = 1
        )

        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowForward,
            contentDescription = content,
            modifier = Modifier.constrainAs(forwardRef) {
                end.linkTo(parent.end, 8.dp)
                top.linkTo(textRef.top)
                bottom.linkTo(textRef.bottom)
            },
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}