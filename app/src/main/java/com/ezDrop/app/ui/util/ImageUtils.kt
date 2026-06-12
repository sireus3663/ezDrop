package com.ezDrop.app.ui.util

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ezDrop.app.R
import com.ezDrop.app.data.util.wearTier

@Composable
fun rememberDrawablePainter(imageRes: String): Painter {
    val context = LocalContext.current
    val id = remember(imageRes) {
        resolveDrawableId(context, imageRes)
    }
    return painterResource(id = id)
}

fun resolveDrawableId(context: Context, imageRes: String): Int {
    return context.resources
        .getIdentifier(imageRes, "drawable", context.packageName)
        .takeIf { it != 0 } ?: R.drawable.img
}

@Composable
fun ItemImage(
    imageRes: String,
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    textColor: Color = Color.White,
    textSize: TextUnit = 18.sp,
    wearTier: String? = null
) {
    val cache by ItemImageMap.urlCache.collectAsState()
    val tier = wearTier ?: "Factory New"
    val url = cache["$name ($tier)"]

    if (url != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = name,
            modifier = modifier,
            contentScale = contentScale,
            success = { state ->
                Image(
                    painter = state.painter,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            },
            error = {
                FallbackImage(imageRes, name, modifier, textColor, textSize)
            }
        )
    } else {
        FallbackImage(imageRes, name, modifier, textColor, textSize)
    }
}

@Composable
private fun FallbackImage(
    imageRes: String,
    name: String,
    modifier: Modifier,
    textColor: Color,
    textSize: TextUnit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(2),
            color = textColor,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ItemImageWithWear(
    imageRes: String,
    name: String,
    wearFloat: Float,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    textColor: Color = Color.White,
    textSize: TextUnit = 18.sp
) {
    val tier = wearTier(wearFloat)
    val overlayRes = when (tier) {
        "Factory New" -> R.drawable.wear_fn
        "Minimal Wear" -> R.drawable.wear_mw
        "Field-Tested" -> R.drawable.wear_ft
        "Well-Worn" -> R.drawable.wear_ww
        "Battle-Scarred" -> R.drawable.wear_bs
        else -> R.drawable.wear_fn
    }
    Box(modifier = modifier) {
        ItemImage(
            imageRes = imageRes,
            name = name,
            modifier = Modifier.matchParentSize(),
            contentScale = contentScale,
            textColor = textColor,
            textSize = textSize,
            wearTier = tier
        )
        Image(
            painter = painterResource(id = overlayRes),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }
}
