package com.ezDrop.app.ui.screen.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezDrop.app.ui.screen.cases.CasesScreen
import com.ezDrop.app.ui.screen.inventory.InventoryScreen
import com.ezDrop.app.ui.screen.profile.ProfileScreen
import com.ezDrop.app.ui.screen.profile.ProfileTopBar
import com.ezDrop.app.viewmodel.MainViewModel
import com.ezDrop.app.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit,
    onNavigateToCase: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selected by remember { mutableStateOf(0) }
    val profileViewModel: ProfileViewModel = viewModel()
    val user = state.user ?: return
    val profileState by profileViewModel.state.collectAsState()
    val avatarUri = user.avatarUri ?: profileState.user?.avatarUri

    LaunchedEffect(profileState.avatarRefresh) {
        viewModel.refreshUser()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        topBar = {
            if (selected == 0) {
                ProfileTopBar(
                    onLogout = onLogout,
                    onSettings = { profileViewModel.startEditing() }
                )
            } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF061733))
                    .padding(vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .requiredWidth(600.dp)
                        .height(100.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 100.dp, y = (-22).dp)
                        .rotate(14f)
                        .clipToBounds()
                        .background(Color(0xFF072C67))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .offset(y = 50.dp)
                            .align(Alignment.TopCenter)
                            .rotate(-28f)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(x = 40.dp, y = (-10).dp)
                            .rotate(-14f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Balance",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${state.balance} $",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .requiredWidth(600.dp)
                        .height(100.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-50).dp)
                        .rotate(-14f)
                        .background(Color(0xFF132748))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(x = (-130).dp, y = (-10).dp)
                            .rotate(14f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D2147))
                                .border(2.dp, Color(0xFF3EC6FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val topAvatarBitmap = remember(avatarUri, profileState.avatarRefresh) {
                                avatarUri?.let { BitmapFactory.decodeFile(it) }
                            }
                            if (topAvatarBitmap != null) {
                                Image(
                                    bitmap = topAvatarBitmap.asImageBitmap(),
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = user.nickname.firstOrNull()?.uppercase() ?: "U",
                                    color = Color(0xFF3EC6FF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = state.nickname.ifEmpty { "User" },
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Level ${state.level}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF040B16))
                    .navigationBarsPadding()
                    .height(60.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                        .clipToBounds()
                ) {
                    Box(
                        modifier = Modifier
                            .requiredHeight(100.dp)
                            .width(20.dp)
                            .align(Alignment.Center)
                            .rotate(20f)
                            .background(
                                if (selected == 1) Color(0xFF3EC6FF)
                                else Color(0xFF2E4A6B)
                            )
                            .clickable { selected = 1 }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selected = 0 }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = if (selected == 0) Color(0xFF3EC6FF)
                            else Color(0xFF2E4A6B)
                        )
                    }

                    Box(modifier = Modifier.size(40.dp)) {}

                    IconButton(onClick = { selected = 2 }) {
                        Icon(
                            imageVector = Icons.Filled.MailOutline,
                            contentDescription = "Inventory",
                            tint = if (selected == 2) Color(0xFF3EC6FF)
                            else Color(0xFF2E4A6B)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF061733)),
            contentAlignment = Alignment.TopCenter
        ) {
            when (selected) {
                0 -> ProfileScreen(profileViewModel = profileViewModel)
                1 -> CasesScreen(onNavigateToCase = onNavigateToCase)
                2 -> InventoryScreen()
            }
        }
    }
}
