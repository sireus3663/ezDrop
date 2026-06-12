package com.ezDrop.app.ui.screen.inventory

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezDrop.app.data.util.wearTier
import com.ezDrop.app.ui.util.ItemImage
import com.ezDrop.app.ui.util.ItemImageMap
import com.ezDrop.app.viewmodel.InventoryDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDetailScreen(
    inventoryId: Long,
    onBack: () -> Unit,
    onSold: () -> Unit,
    viewModel: InventoryDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(inventoryId) {
        viewModel.loadEntry(inventoryId)
    }

    LaunchedEffect(state.sold) {
        if (state.sold) onSold()
    }

    Scaffold(
        containerColor = Color(0xFF061733),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            text = "\u2190",
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D2147)
                )
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF3EC6FF))
            }
            return@Scaffold
        }

        val entry = state.entry ?: return@Scaffold
        val color = rarityColor(entry.rarity)
        val tier = wearTier(entry.wearFloat)
        val descs by ItemImageMap.descriptionCache.collectAsState()
        val cleanDesc = descs[entry.name]?.let { raw ->
            Html.fromHtml(raw, Html.FROM_HTML_MODE_LEGACY).toString().trim()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ItemImage(
                imageRes = entry.imageRes,
                name = entry.name,
                wearTier = tier,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentScale = ContentScale.Fit,
                textColor = color,
                textSize = 64.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = entry.name,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${entry.category.replaceFirstChar { it.uppercase() }} \u00B7 $tier",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Float: ${"%.8f".format(entry.wearFloat)}",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${entry.finalPrice}$",
                color = color,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            if (!cleanDesc.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = cleanDesc,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.sellItem() },
                enabled = !state.isSelling,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3EC6FF))
            ) {
                if (state.isSelling) {
                    CircularProgressIndicator(
                        color = Color(0xFF061733),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Sell ${entry.finalPrice}$",
                        color = Color(0xFF061733),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
