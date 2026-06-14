package com.ezDrop.app.ui.screen.cases

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezDrop.app.data.db.dao.CaseItemWithDetails
import com.ezDrop.app.data.util.wearTier
import com.ezDrop.app.ui.util.ItemImage
import com.ezDrop.app.ui.util.ItemImageWithWear
import com.ezDrop.app.ui.util.rememberDrawablePainter
import com.ezDrop.app.viewmodel.CaseDetail
import com.ezDrop.app.viewmodel.CaseOpeningResult
import com.ezDrop.app.viewmodel.CaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    caseId: Long,
    onBack: () -> Unit,
    onBalanceChanged: () -> Unit,
    caseViewModel: CaseViewModel = viewModel()
) {
    val detail by caseViewModel.detail.collectAsState()
    val openingState by caseViewModel.openingState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(caseId) {
        caseViewModel.loadCaseDetail(caseId)
    }

    LaunchedEffect(openingState.error) {
        openingState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(openingState.showResults || openingState.isAnimating) {
        if (openingState.showResults) onBalanceChanged()
    }

    val data = detail ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF061733)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF3EC6FF))
        }
        return
    }

    var caseCount by remember { mutableStateOf(1) }
    if (data.caseInfo.price == 0 && caseCount > 1) {
        caseCount = 1
    }
    var backClicked by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF061733)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF061733))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clipToBounds()
                    .background(Color(0xFF0D2147)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (!backClicked) {
                            backClicked = true
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(y = (-8).dp)
                ) {
                    Text(
                        text = "\u2190",
                        color = Color.White,
                        fontSize = 22.sp
                    )
                }

                if (openingState.results.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.Black.copy(alpha = 0.25f))
                    ) {
                        CaseOpenAnimation(
                            count = openingState.count,
                            allItems = data.items,
                            winners = openingState.results.map { it.wonItem!! },
                            onAnimationEnd = { caseViewModel.onAnimationEnd() }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberDrawablePainter(data.caseInfo.imageRes),
                            contentDescription = data.caseInfo.name,
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = data.caseInfo.name,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${data.caseInfo.price} $ \u00B7 Level ${data.caseInfo.requiredLevel}",
                            color = Color(0xFF3EC6FF),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Possible Drops",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(data.items) { item ->
                    ItemDropCard(item = item, totalWeight = data.totalWeight)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(1, 2, 4).forEach { count ->
                    val selected = caseCount == count
                    Button(
                        onClick = { caseCount = count },
                        enabled = !openingState.isAnimating && (data.caseInfo.price > 0 || count == 1),
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Color(0xFF3EC6FF) else Color(0xFF2E4A6B)
                        )
                    ) {
                        Text(
                            text = "${count}x",
                            color = if (selected) Color(0xFF061733) else Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Button(
                onClick = { caseViewModel.startOpening(caseCount) },
                enabled = !openingState.isAnimating,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3EC6FF))
            ) {
                Text(
                    text = "Open Case (${data.caseInfo.price * caseCount} $)",
                    color = Color(0xFF061733),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }

    if (openingState.showResults) {
        MultiResultSheet(
            results = openingState.results,
            onDismiss = { caseViewModel.resetOpeningState() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultiResultSheet(
    results: List<CaseOpeningResult>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D2147),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YOU WON!",
                color = Color(0xFF3EC6FF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (results.size <= 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    results.forEach { result ->
                        WonItemCardCompact(
                            item = result.wonItem!!,
                            wearFloat = result.wearFloat,
                            wearTier = result.wearTier,
                            finalPrice = result.finalPrice,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(results) { result ->
                        WonItemCardCompact(
                            item = result.wonItem!!,
                            wearFloat = result.wearFloat,
                            wearTier = result.wearTier,
                            finalPrice = result.finalPrice,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3EC6FF))
            ) {
                Text(
                    text = "Close",
                    color = Color(0xFF061733),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WonItemCardCompact(
    item: CaseItemWithDetails,
    wearFloat: Float,
    wearTier: String,
    finalPrice: Int,
    modifier: Modifier = Modifier
) {
    val color = rarityColor(item.rarity)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ItemImageWithWear(
                imageRes = item.imageRes,
                name = item.name,
                wearFloat = wearFloat,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentScale = ContentScale.Fit,
                textColor = color,
                textSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Text(
                text = "$wearTier",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
            Text(
                text = "$finalPrice $",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ItemDropCard(item: CaseItemWithDetails, totalWeight: Int) {
    val chance = if (totalWeight > 0)
        "%.2f".format(item.dropWeight.toFloat() / totalWeight * 100)
    else
        "0.00"

    val color = rarityColor(item.rarity)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2147))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemImage(
                imageRes = item.imageRes,
                name = item.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentScale = ContentScale.Fit,
                textColor = color,
                textSize = 14.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = item.rarity,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Text(
                text = "$chance%",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
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
