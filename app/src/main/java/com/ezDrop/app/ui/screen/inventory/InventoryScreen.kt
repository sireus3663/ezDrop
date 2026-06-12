package com.ezDrop.app.ui.screen.inventory

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezDrop.app.data.db.dao.InventoryItemEntry
import com.ezDrop.app.data.util.wearTier
import com.ezDrop.app.ui.util.ItemImage
import com.ezDrop.app.ui.util.ItemImageWithWear
import com.ezDrop.app.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    onBalanceChanged: () -> Unit,
    onItemClick: (Long) -> Unit = {},
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    val state by inventoryViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        inventoryViewModel.loadInventory()
    }

    LaunchedEffect(state.balanceVersion) {
        if (state.balanceVersion > 0) onBalanceChanged()
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF3EC6FF))
        }
        return
    }

    if (state.items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Inventory is empty",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(state.items, key = { it.inventoryId }) { entry ->
            InventoryItemCard(
                entry = entry,
                isSelling = state.sellingInventoryId == entry.inventoryId,
                onSell = { inventoryViewModel.sellItem(entry) },
                onClick = { onItemClick(entry.inventoryId) }
            )
        }
    }
}

@Composable
private fun InventoryItemCard(
    entry: InventoryItemEntry,
    isSelling: Boolean,
    onSell: () -> Unit,
    onClick: () -> Unit
) {
    val color = rarityColor(entry.rarity)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2147))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemImageWithWear(
                imageRes = entry.imageRes,
                name = entry.name,
                wearFloat = entry.wearFloat,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentScale = ContentScale.Fit,
                textColor = color,
                textSize = 18.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${wearTier(entry.wearFloat)} \u00B7 ${entry.rarity}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onSell,
                enabled = !isSelling,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E4A6B)
                ),
                modifier = Modifier.height(38.dp)
            ) {
                if (isSelling) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "Sell ${entry.finalPrice}$",
                        color = Color(0xFF3EC6FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
