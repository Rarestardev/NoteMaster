package com.rarestardev.notemaster.feature

import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.rarestardev.notemaster.enums.ImageSize

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResizableImageItem(
    uri: Uri,
    onDelete: () -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }
    var imageSize by remember { mutableStateOf(ImageSize.MEDIUM) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { showOptions = true }
            )
    ) {
        GlideImage(
            imageUrl = uri.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .height(imageSize.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        if (showOptions) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 8.dp)
                    .background(Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Image Size", style = MaterialTheme.typography.labelLarge)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ImageSize.entries.forEach { size ->
                            OutlinedButton(
                                onClick = {
                                    imageSize = size
                                    showOptions = false
                                }
                            ) {
                                Text(size.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onDelete()
                            showOptions = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete image", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun GlideImage(
    imageUrl: String,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = when (contentScale) {
                    ContentScale.Crop -> ImageView.ScaleType.CENTER_CROP
                    ContentScale.Fit -> ImageView.ScaleType.FIT_CENTER
                    else -> ImageView.ScaleType.CENTER_INSIDE
                }
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