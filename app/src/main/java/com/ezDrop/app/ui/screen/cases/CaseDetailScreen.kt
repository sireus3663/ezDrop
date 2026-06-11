package com.ezDrop.app.ui.screen.cases

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezDrop.app.data.db.dao.CaseItemWithDetails
import com.ezDrop.app.ui.util.ItemImage
import com.ezDrop.app.ui.util.rememberDrawablePainter
import com.ezDrop.app.viewmodel.CaseDetail
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
    val openingResult by caseViewModel.openingResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(caseId) {
        caseViewModel.loadCaseDetail(caseId)
    }

    LaunchedEffect(openingResult) {
        if (openingResult?.isSuccess == true) {
            onBalanceChanged()
        } else if (openingResult?.error != null) {
            snackbarHostState.showSnackbar(openingResult!!.error!!)
        }
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

    val wonItem = openingResult?.wonItem
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(wonItem) {
        if (wonItem != null) {
            showResult = true
        }
    }

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
                    .background(Color(0xFF0D2147))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

            Button(
                onClick = { caseViewModel.openCase() },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3EC6FF))
            ) {
                Text(
                    text = "Open Case (${data.caseInfo.price} $)",
                    color = Color(0xFF061733),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }

    if (showResult && wonItem != null) {
        ResultSheet(
            item = wonItem,
            onDismiss = {
                showResult = false
                caseViewModel.resetOpeningResult()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultSheet(
    item: CaseItemWithDetails,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D2147),
        contentColor = Color.White
    ) {
        WonItemCard(item = item)
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
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

@Composable
private fun WonItemCard(item: CaseItemWithDetails) {
    val color = rarityColor(item.rarity)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YOU WON!",
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ItemImage(
                imageRes = item.imageRes,
                name = item.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentScale = ContentScale.Fit,
                textColor = color,
                textSize = 28.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.name,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${item.quality} \u00B7 ${item.category}",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp
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

    val rarityColor = rarityColor(item.rarity)

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
                    .background(rarityColor.copy(alpha = 0.2f)),
                contentScale = ContentScale.Fit,
                textColor = rarityColor,
                textSize = 14.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = rarityColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = item.quality,
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
    else -> Color.White
}
