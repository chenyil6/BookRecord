package com.example.BookRecord

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TopAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.BookRecord.ui.theme.BookRecordTheme
import com.example.BookRecord.ui.theme.ThemeManager


@Composable
fun NotesScreen( // 重命名为 NotesScreen
    navController: NavController,
    bookId:Int,
    modifier: Modifier = Modifier,
) {
    // 示例笔记数据
    val viewModel = LocalNotesViewModel.current

    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val elderlyMode = LocalElderlyMode.current
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    val isElderlyModeEnabled =
        remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    CompositionLocalProvider(LocalElderlyMode provides isElderlyModeEnabled) {
        BookRecordTheme(themeColor) {
            // 通知 ViewModel 更新当前 bookId
            LaunchedEffect(bookId) {
                viewModel.setBookId(bookId)
            }

            // 观察 LiveData 并将其转换为 Compose 可用的状态
            val notesByBookId = viewModel.notesByBookId.observeAsState(initial = emptyList())
            if (elderlyMode.value) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.height(70.dp), // 设置自定义高度为 80dp
                            title = {
                                Text(
                                    text = "View Notes",
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
                                    },
                                    modifier = Modifier.size(50.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.ArrowBack,
                                        contentDescription = "返回",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            },
                            backgroundColor = Color.White,
                            elevation = 8.dp
                        )
                    }
                ) { paddingValues ->
                    // 笔记列表
                    NoteList(
                        notes = notesByBookId.value,
                        modifier = modifier.padding(paddingValues).padding(16.dp)
                    )

                }
            }
            else {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.height(70.dp), // 设置自定义高度为 80dp
                            title = {
                                Text(
                                    text = "View Notes",
                                    fontSize = 28.sp,
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
                ) { paddingValues ->
                    // 笔记列表
                    NoteList(
                        notes = notesByBookId.value,
                        modifier = modifier.padding(paddingValues).padding(16.dp)
                    )

                }
            }
        }
    }
}

@Composable
fun NoteList(notes: List<Note>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val elderlyMode = LocalElderlyMode.current
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    val isElderlyModeEnabled =
        remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    CompositionLocalProvider(LocalElderlyMode provides isElderlyModeEnabled) {
        BookRecordTheme(themeColor) {
            if (elderlyMode.value) {
                LazyColumn(modifier = modifier) {
                    items(items = notes, key = { note -> note.hashCode() }) { note ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = note.content, fontSize = 24.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(modifier = modifier) {
                    items(items = notes, key = { note -> note.hashCode() }) { note ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = note.content, fontSize = 18.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

