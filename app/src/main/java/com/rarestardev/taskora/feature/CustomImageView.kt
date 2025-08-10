package com.rarestardev.taskora.feature

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.ImagePreviewActivity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomImageView(
    uri: String,
    showDelete: Boolean,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.large)
            .height(220.dp)
    ) {
        val (imageRef, deleteRef) = createRefs()
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clickable{
                    if (uri.isNotEmpty()){
                        val intent = Intent(context, ImagePreviewActivity::class.java).apply {
                            putExtra("uri",uri)
                        }

                        context.startActivity(intent)
                    }
                }
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            alignment = Alignment.Center
        )

        if (showDelete) {
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .constrainAs(deleteRef) {
                        end.linkTo(parent.end, 8.dp)
                        top.linkTo(parent.top, 8.dp)
                    }
                    .background(MaterialTheme.colorScheme.onSecondaryContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun SmallCustomImageView(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds,
        alignment = Alignment.Center
    )
}