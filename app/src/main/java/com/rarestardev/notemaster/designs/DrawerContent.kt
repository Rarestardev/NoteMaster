@file:Suppress("UNUSED_EXPRESSION")

package com.rarestardev.notemaster.designs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import kotlinx.coroutines.launch

@Preview
@Composable
private fun PreviewDrawerContent() {
    NoteMasterTheme {
        DrawerContent(rememberDrawerState(DrawerValue.Open))
    }
}

@Composable
fun DrawerContent(drawerState: DrawerState) {
    val secondColorAnyMode = MaterialTheme.colorScheme.secondary
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxHeight()
            .width(270.dp)
            .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
            .background(secondColorAnyMode)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        )
        {
            DrawerHeaderLayout {
                scope.launch {
                    drawerState.close()
                    Toast.makeText(context, "ProfileView", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            DrawerItems("Settings", Icons.Default.Settings) {
                scope.launch {
                    drawerState.close()
                    showToast("Settings", context)
                }
            }
            DrawerItems("Edit", Icons.Default.Edit) {
                scope.launch {
                    drawerState.close()
                    showToast("Edit", context)
                }
            }
            DrawerItems("Exit", Icons.AutoMirrored.Filled.ExitToApp) {
                scope.launch {
                    drawerState.close()
                    showToast("Exit", context)
                }
            }

        }
    }
}

private fun showToast(title: String, context: Context) {
    Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
}

@Composable
private fun DrawerHeaderLayout(onClickProfile: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        val (profileRef, spacerRef) = createRefs()

        Spacer(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .constrainAs(spacerRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
        )

        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Profile",
            modifier = Modifier
                .size(80.dp)
                .constrainAs(profileRef) {
                    start.linkTo(parent.start)
                    top.linkTo(spacerRef.bottom)
                    end.linkTo(parent.end)
                }
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClickProfile }
        )
    }
}


@Composable
private fun DrawerItems(title: String, icon: ImageVector, onClick: () -> Unit) {
    val color = colorResource(R.color.drawer_text_icon_color)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color
        )


        Text(
            text = title,
            color = color,
            modifier = Modifier
                .padding(end = 10.dp, start = 10.dp)
        )
    }
}