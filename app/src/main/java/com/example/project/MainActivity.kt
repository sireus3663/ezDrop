package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.ui.theme.ProjectTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selected by remember { mutableStateOf(0) }
            ProjectTheme {
                Scaffold(
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        Box(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFF061733))
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
                                //окно баланса
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .offset(y = (50).dp)
                                        .align(Alignment.TopCenter)
                                        .rotate(-28f)
                                        .background(
                                            brush = Brush.verticalGradient( //градиент с нейронки
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
                                        text = "Баланс",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "1 000 ₽",
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
                                    .offset(y = (-45).dp)
                                    .rotate(-14f)
                                    .background(Color(0xFF132748))
                            ) {
                                // Окно пользователя
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(x = (-100).dp, y = (-10).dp)
                                        .rotate(14f)
                                ) {
                                    // Аватарка
                                    Image(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Аватар",
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        colorFilter = ColorFilter.tint(Color.White)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Имя и уровень
                                    Column {
                                        Text(
                                            text = "Иван Иванов",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Уровень 5",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(Color(0xFF040B16))
                                .clip(RoundedCornerShape(20.dp))
                        ) {

                            // 🔷 Центральный "срезанный" прямоугольник
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
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
                                            if (selected == 1)
                                                Color(0xFF3EC6FF)
                                            else
                                                Color(0xFF2E4A6B)
                                        )
                                        .clickable { selected = 1 }
                                )
                            }

                            // 🔘 Кнопки
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 40.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Левая кнопка
                                IconButton(onClick = { selected = 0 }) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (selected == 0)
                                            Color(0xFF3EC6FF)
                                        else
                                            Color(0xFF2E4A6B)
                                    )
                                }

                                // Центр (можно сделать кнопку тоже)
                                Box(modifier = Modifier.size(40.dp)) {}

                                // Правая кнопка
                                IconButton(onClick = { selected = 2 }) {
                                    Icon(
                                        imageVector = Icons.Filled.MailOutline,
                                        contentDescription = null,
                                        tint = if (selected == 2)
                                            Color(0xFF3EC6FF)
                                        else
                                            Color(0xFF2E4A6B)
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->

                    Box(modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(Color(0xFF061733)),
                        contentAlignment = Alignment.TopCenter
                    ) {

                        when (selected) {
                            0 -> screen_1()
                            1 -> screen_2()
                            2 -> screen_3()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestRotate() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .rotate(30f)
                .background(Color.Blue)
        )
    }
}
@Composable
fun screen_1() {
    Text("экран_1", color = Color.White)
}
@Composable
fun screen_2() {
    Text("экран_2", color = Color.White)
}
@Composable
fun screen_3() {
    Text("экран_3", color = Color.White)
}
