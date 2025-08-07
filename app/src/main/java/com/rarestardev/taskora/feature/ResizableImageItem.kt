package com.rarestardev.taskora.feature

import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.Glide
import com.rarestardev.taskora.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResizableImageItem(
    uri: Uri,
    showDelete: Boolean,
    onDelete: () -> Unit
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(220.dp)
    ) {
        val (imageRef, deleteRef) = createRefs()
        GlideImage(
            imageUrl = uri.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        if (showDelete){
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
fun GlideImage(
    imageUrl: String,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)
        },
        modifier = modifier
    )
}