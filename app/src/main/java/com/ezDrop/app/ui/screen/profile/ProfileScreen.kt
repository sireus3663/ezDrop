package com.ezDrop.app.ui.screen.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezDrop.app.viewmodel.MainViewModel
import com.ezDrop.app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
) {
    val state by profileViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
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

    val user = state.user ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF061733))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            val avatarBitmap = remember(user.avatarUri, state.avatarRefresh) {
                user.avatarUri?.let { BitmapFactory.decodeFile(it) }
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D2147))
                    .border(2.dp, Color(0xFF3EC6FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (avatarBitmap != null) {
                    Image(
                        bitmap = avatarBitmap.asImageBitmap(),
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

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = user.nickname,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Level ${user.level}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val progress = if (user.xpNeed > 0)
                    (user.xp.toFloat() / user.xpNeed).coerceIn(0f, 1f) else 0f

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0D2147))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(Color(0xFF3EC6FF), RoundedCornerShape(4.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "XP: ${user.xp} / ${user.xpNeed}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        StatCard(
            number = user.balance.toLong(),
            label = "balance",
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatCard(
            number = state.inventoryValue.toLong(),
            label = "inventory coast",
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatCard(
            number = user.netWorth,
            label = "net worth",
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (state.editing) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { profileViewModel.stopEditing() },
            sheetState = sheetState,
            containerColor = Color(0xFF0D2147),
            contentColor = Color.White
        ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    val context = LocalContext.current
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        uri?.let { profileViewModel.selectAvatar(it) }
                    }

                    val previewBitmap = remember(state.pendingAvatarUri) {
                        state.pendingAvatarUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { stream ->
                                BitmapFactory.decodeStream(stream)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF061733),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { launcher.launch("image/*") }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF061733))
                                .border(1.dp, Color(0xFF3EC6FF), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (previewBitmap != null) {
                                Image(
                                    bitmap = previewBitmap.asImageBitmap(),
                                    contentDescription = "Preview",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = "+",
                                    color = Color(0xFF3EC6FF),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Change Avatar",
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nickname",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.editNickname,
                    onValueChange = { profileViewModel.updateEditNickname(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF3EC6FF),
                        unfocusedBorderColor = Color(0xFF2E4A6B),
                        focusedLabelColor = Color(0xFF3EC6FF),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )

                state.error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { profileViewModel.saveChanges() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.saving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3EC6FF)
                    )
                ) {
                    if (state.saving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF061733)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { profileViewModel.stopEditing() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileTopBar(
    onLogout: () -> Unit,
    onSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF061733))
            .padding(vertical = 10.dp)
    ){
        Box(
            modifier = Modifier
                .requiredWidth(600.dp)
                .height(100.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .rotate(-20f)
                .background(Color(0xFF132748))
        ) {
            Box(
                modifier = Modifier
                    .rotate(20f)
                    .size(width = 140.dp, height = 44.dp)
                    .offset(x = (100).dp, y = (10).dp)
                    .clickable { onLogout() },
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "<- log out",
                    color = Color.Red,
                    fontSize = 15.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF3EC6FF), Color(0xFF2E8BFF))
                    ),
                    shape = RoundedCornerShape(50)
                )
                .clickable { onSettings() }
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }


    /*Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 130.dp, height = 44.dp)
                .background(Color(0xFF072C67))
                .clickable { onLogout() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Logout",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF3EC6FF), Color(0xFF2E8BFF))
                    ),
                    shape = RoundedCornerShape(50)
                )
                .clickable { onSettings() }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⚙",
                    fontSize = 16.sp,
                    color = Color(0xFF061733)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Настроить",
                    color = Color(0xFF061733),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }*/
}

@Composable
private fun StatCard(
    number: Long,
    label: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color = Color(0xFF0D2147).copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp)
            )
            //.padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clipToBounds()
                    .padding(10.dp)
            ){
                Text(
                    text = number.toString(),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clipToBounds()
                    .background(
                        Color(0xFF13345E),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
