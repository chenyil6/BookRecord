package com.example.BookRecord

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager

import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.automirrored.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.BookRecord.ui.theme.BlueColorScheme
import com.example.BookRecord.ui.theme.BookRecordTheme
import com.example.BookRecord.ui.theme.GreenColorScheme
import com.example.BookRecord.ui.theme.PurpleColorScheme
import com.example.BookRecord.ui.theme.ThemeManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingScreen(
    navController: NavController, googleSignInClient: GoogleSignInClient, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    val isElderlyModeEnabled =
        remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    var selectedColor by remember { mutableStateOf(themeColor.value.primary) }
    CompositionLocalProvider(LocalElderlyMode provides isElderlyModeEnabled) {
        BookRecordTheme(themeColor) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        modifier = Modifier.height(70.dp), // 设置自定义高度为 80dp
                        title = {
                            Text(
                                text = "Settings",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 15.dp)

                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "返回"
                                )
                            }
                        },
                        backgroundColor = Color.White,
                        elevation = 8.dp
                    )
                }
            ) {

                Column {
                    Spacer(modifier = Modifier.height(100.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 17.dp, horizontal = 16.dp)
                                    .clickable {
                                        val newMode = !isElderlyModeEnabled.value
                                        isElderlyModeEnabled.value = newMode
                                        sharedPreferences.edit()
                                            .putBoolean("elderly_mode_enabled", newMode).apply()
                                    }
                            ) {
                                Text(
                                    text = if (isElderlyModeEnabled.value) "   Quit Elderly-Friendly Mode" else "   Elderly-Friendly Mode",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = if (isElderlyModeEnabled.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isElderlyModeEnabled.value) "Disable Elderly-Friendly Mode" else "Enable Elderly-Friendly Mode",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 17.dp, horizontal = 16.dp)
                            ) {
                                Text(
                                    text = "   Theme",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            }
                            Column {
                                ThemeSelectionRow(selectedColor, onChangeColor = { colorName ->
                                    ThemeManager.setColorScheme(context, colorName)
                                    themeColor.value = ThemeManager.getColorScheme(context)
                                    selectedColor = themeColor.value.secondaryContainer  // 更新选中色
                                })
                            }
                            Spacer(modifier = Modifier.height(22.dp))
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White)
                            .clickable {
                                // 点击 Log Out 返回登录界面
                                FirebaseAuth.getInstance().signOut()
                                googleSignInClient.signOut()
                                navController.navigate("LoginScreen") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 17.dp, horizontal = 16.dp)
                            ) {
                                Text(
                                    text = "   Log Out",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                                Spacer(modifier = Modifier.weight(1f)) // 添加空白间隔
                                Icon(
                                    imageVector = Icons.Default.ExitToApp, // 使用退出应用图标
                                    contentDescription = "   Logout Icon",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp) // 设置图标大小
                                )
                            }
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                        }
                    }

                }
            }
            LaunchedEffect(key1 = themeColor.value) {
                themeColor.value = ThemeManager.getColorScheme(context)
                selectedColor = themeColor.value.secondaryContainer
            }
        }
    }
}

@Composable
fun ThemeSelectionRow(selectedColor: Color, onChangeColor: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Spacer(modifier = Modifier.width(32.dp))
        ThemeColorOption(PurpleColorScheme.secondaryContainer, selectedColor, "purple", onChangeColor)
        Spacer(modifier = Modifier.width(24.dp))
        ThemeColorOption(BlueColorScheme.secondaryContainer, selectedColor, "blue", onChangeColor)
        Spacer(modifier = Modifier.width(24.dp))
        ThemeColorOption(GreenColorScheme.secondaryContainer, selectedColor, "green", onChangeColor)
    }
}


@Composable
fun ThemeColorOption(color: Color, selectedColor: Color, themeName: String, onChangeColor: (String) -> Unit) {
    val isSelected = color == selectedColor
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(100.dp, 70.dp)
                .background(color, RoundedCornerShape(8.dp))
                .clickable { onChangeColor(themeName) }
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.ChromeReaderMode, // 确认使用正确的图标
                contentDescription = "Selected",
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


