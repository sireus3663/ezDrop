package com.ezDrop.app.ui.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.ezDrop.app.R

@Composable
fun rememberDrawablePainter(imageRes: String): Painter {
    val context = LocalContext.current
    val id = remember(imageRes) {
        resolveDrawableId(context, imageRes)
    }
    return painterResource(id = id)
}

@Composable
fun rememberNullablePainter(imageRes: String): Painter? {
    val context = LocalContext.current
    val id = remember(imageRes) {
        context.resources
            .getIdentifier(imageRes, "drawable", context.packageName)
            .takeIf { it != 0 }
    }
    return id?.let { painterResource(id = it) }
}

fun resolveDrawableId(context: Context, imageRes: String): Int {
    return context.resources
        .getIdentifier(imageRes, "drawable", context.packageName)
        .takeIf { it != 0 } ?: R.drawable.img
}
