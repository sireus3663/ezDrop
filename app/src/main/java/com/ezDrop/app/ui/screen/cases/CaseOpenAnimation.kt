package com.ezDrop.app.ui.screen.cases

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezDrop.app.data.db.dao.CaseItemWithDetails
import com.ezDrop.app.ui.util.ItemImage
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

private val SLOT_EASING = CubicBezierEasing(0.1f, 0.9f, 0.2f, 1.0f)

@Composable
fun CaseOpenAnimation(
    count: Int,
    allItems: List<CaseItemWithDetails>,
    winners: List<CaseItemWithDetails>,
    onAnimationEnd: () -> Unit
) {
    var finishedCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(count) {
        finishedCount = 0
    }

    LaunchedEffect(finishedCount) {
        if (count > 0 && finishedCount >= count) {
            delay(600)
            onAnimationEnd()
        }
    }

    if (count == 1) {
        ItemScrollRow(
            allItems = allItems,
            winner = winners[0],
            onFinished = { finishedCount++ }
        )
    } else {
        val rowHeight = if (count == 2) 36.dp else 24.dp
        val stripeWidth = if (count == 2) 144.dp else 96.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(count) { rowIndex ->
                StripeScrollRow(
                    allItems = allItems,
                    winner = winners[rowIndex],
                    heightDp = rowHeight,
                    itemWidthDp = stripeWidth,
                    onFinished = { finishedCount++ }
                )
            }
        }
    }
}

@Composable
private fun ItemScrollRow(
    allItems: List<CaseItemWithDetails>,
    winner: CaseItemWithDetails,
    onFinished: () -> Unit
) {
    val targetIndex = 20
    val itemWidthDp = 112.dp
    val scrollItems = remember(allItems, winner) {
        buildScrollItems(allItems, winner, targetIndex)
    }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var boxWidth by remember { mutableIntStateOf(0) }

    LaunchedEffect(winner, boxWidth) {
        if (boxWidth == 0) return@LaunchedEffect
        if (scrollState.value == 0) {
            val itemWidthPx = with(density) { itemWidthDp.toPx() }
            val targetScroll = (targetIndex * itemWidthPx - (boxWidth / 2f - itemWidthPx / 2f)).roundToInt()
            scrollState.animateScrollTo(
                targetScroll,
                tween(durationMillis = 8000, easing = SLOT_EASING)
            )
        }
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { boxWidth = it.width }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .horizontalScroll(scrollState, enabled = false)
                .clipToBounds()
                .background(Color(0xFF061733), RoundedCornerShape(8.dp))
        ) {
            scrollItems.forEachIndexed { idx, item ->
                val color = rarityColor(item.rarity)
                Box(
                    modifier = Modifier
                        .width(itemWidthDp)
                        .fillMaxHeight()
                        .background(color.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    ItemImage(
                        imageRes = item.imageRes,
                        name = item.name,
                        modifier = Modifier.size(112.dp),
                        wearTier = "Factory New",
                        contentScale = ContentScale.Fit,
                        textColor = Color.White.copy(alpha = 0.5f),
                        textSize = 12.sp
                    )
                }
                if (idx < scrollItems.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF0D2147))
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.dp)
                .height(112.dp)
                .background(Color.White.copy(alpha = 0.5f))
        )
    }
}

@Composable
private fun StripeScrollRow(
    allItems: List<CaseItemWithDetails>,
    winner: CaseItemWithDetails,
    heightDp: Dp,
    itemWidthDp: Dp,
    onFinished: () -> Unit
) {
    val targetIndex = 20
    val scrollItems = remember(allItems, winner) {
        buildScrollItems(allItems, winner, targetIndex)
    }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var boxWidth by remember { mutableIntStateOf(0) }

    LaunchedEffect(winner, boxWidth) {
        if (boxWidth == 0) return@LaunchedEffect
        if (scrollState.value == 0) {
            val itemWidthPx = with(density) { itemWidthDp.toPx() }
            val targetScroll = (targetIndex * itemWidthPx - (boxWidth / 2f - itemWidthPx / 2f)).roundToInt()
            scrollState.animateScrollTo(
                targetScroll,
                tween(durationMillis = 8000, easing = SLOT_EASING)
            )
        }
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { boxWidth = it.width }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp)
                .horizontalScroll(scrollState, enabled = false)
                .clipToBounds()
                .background(Color(0xFF061733))
        ) {
            scrollItems.forEachIndexed { idx, item ->
                val color = rarityColor(item.rarity)
                Box(
                    modifier = Modifier
                        .width(itemWidthDp)
                        .fillMaxHeight()
                        .background(color.copy(alpha = 0.7f))
                )
                if (idx < scrollItems.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF0D2147))
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.dp)
                .height(heightDp)
                .background(Color.White.copy(alpha = 0.5f))
        )
    }
}

private fun buildScrollItems(
    allItems: List<CaseItemWithDetails>,
    winner: CaseItemWithDetails,
    targetIndex: Int
): List<CaseItemWithDetails> {
    val size = 30
    val list = MutableList<CaseItemWithDetails>(size) { i ->
        if (i == targetIndex) winner
        else allItems.random(Random) //иллюзия крутого дропа, можно заменить на нормальный
    }
    return list
}

private fun shortenName(name: String): String {
    val parts = name.split(" | ")
    return if (parts.size > 1) parts.last().take(10) else name.take(10)
}

private fun rarityColor(rarity: String): Color = when (rarity) {
    "common" -> Color(0xFFB0B0B0)
    "uncommon" -> Color(0xFF5E98D9)
    "rare" -> Color(0xFF4B69FF)
    "epic" -> Color(0xFF8847FF)
    "legendary" -> Color(0xFFFFD700)
    "souvenir" -> Color.White
    else -> Color.White
}
