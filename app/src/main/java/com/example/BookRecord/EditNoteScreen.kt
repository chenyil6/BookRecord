package com.example.BookRecord

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.BookRecord.ui.theme.BookRecordTheme
import com.example.BookRecord.ui.theme.ThemeManager


@Composable
fun EditNotesScreen(
    navController: NavController,
    bookId: Int,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingContent by remember { mutableStateOf("") }
    var currentEditingNoteId by remember { mutableStateOf<Int?>(null) }

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
            if (elderlyMode.value) {
                Scaffold(
                    floatingActionButton = {
                        SmallAddButton(onClick = {
                            // 在这里定义点击悬浮按钮后的动作
                            editingContent = ""
                            currentEditingNoteId = null // 表示添加新笔记
                            showDialog = true
                        })
                    },
                    floatingActionButtonPosition = FabPosition.End, // 将按钮放在右下角
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.height(70.dp), // 设置自定义高度为 80dp
                            title = {
                                Text(
                                    text = "Edit Notes",
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
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // 显示添加或编辑笔记的对话框
                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text(if (currentEditingNoteId == null) "Add Note" else "Edit Note", fontSize = 30.sp) },
                                text = {
                                    TextField(
                                        value = editingContent,
                                        onValueChange = { editingContent = it },
                                        label = { Text("Content", fontSize = 18.sp) },
                                        textStyle = TextStyle(fontSize = 20.sp)
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (currentEditingNoteId == null) {
                                                // 添加新笔记
                                                viewModel.addNote(editingContent, bookId = bookId)
                                            } else {
                                                // 更新现有笔记
                                                currentEditingNoteId?.let { noteId ->
                                                    // 注意：这里同样假设 bookId 不会是 null，以及你有一个接收 noteId 和 noteContent 的 editNote 方法。
                                                    // 如果你的 editNote 方法需要不同的参数，请相应地调整。
                                                    viewModel.editNote(
                                                        noteId = noteId,
                                                        editingContent,
                                                        bookId = bookId
                                                    ) // 如果 bookId 是 null，则需要决定如何处理
                                                }
                                            }
                                            showDialog = false
                                        }
                                    ) { Text("  OK  ", fontSize = 20.sp) }
                                },
                                dismissButton = {
                                    Button(onClick = { showDialog = false }) { Text("Cancel", fontSize = 20.sp) }
                                }
                            )
                        }

                        // 观察 LiveData 并将其转换为 Compose 可用的状态
                        val notesByBookId =
                            viewModel.notesByBookId.observeAsState(initial = emptyList())


                        // 笔记列表...
                        NotesListScreen(
                            notes = notesByBookId.value,
                            onNoteClick = { note ->
                                editingContent = note.content
                                currentEditingNoteId = note.id
                                showDialog = true
                            },
                            onDeleteClick = { note ->
                                viewModel.deleteNote(note)
                            }
                        )
                    }
                }
            }
            else {Scaffold(
                floatingActionButton = {
                    SmallAddButton(onClick = {
                        // 在这里定义点击悬浮按钮后的动作
                        editingContent = ""
                        currentEditingNoteId = null // 表示添加新笔记
                        showDialog = true
                    })
                },
                floatingActionButtonPosition = FabPosition.End, // 将按钮放在右下角
                topBar = {
                    TopAppBar(
                        modifier = Modifier.height(70.dp), // 设置自定义高度为 80dp
                        title = {
                            Text(
                                text = "Edit Notes",
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
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // 显示添加或编辑笔记的对话框
                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text(if (currentEditingNoteId == null) "Add Note" else "Edit Note") },
                                text = {
                                    TextField(
                                        value = editingContent,
                                        onValueChange = { editingContent = it },
                                        label = { Text("Content") }
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (currentEditingNoteId == null) {
                                                // 添加新笔记
                                                viewModel.addNote(editingContent, bookId = bookId)
                                            } else {
                                                // 更新现有笔记
                                                currentEditingNoteId?.let { noteId ->
                                                    // 注意：这里同样假设 bookId 不会是 null，以及你有一个接收 noteId 和 noteContent 的 editNote 方法。
                                                    // 如果你的 editNote 方法需要不同的参数，请相应地调整。
                                                    viewModel.editNote(
                                                        noteId = noteId,
                                                        editingContent,
                                                        bookId = bookId
                                                    ) // 如果 bookId 是 null，则需要决定如何处理
                                                }
                                            }
                                            showDialog = false
                                        }
                                    ) { Text("OK") }
                                },
                                dismissButton = {
                                    Button(onClick = { showDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        // 观察 LiveData 并将其转换为 Compose 可用的状态
                        val notesByBookId =
                            viewModel.notesByBookId.observeAsState(initial = emptyList())


                        // 笔记列表...
                        NotesListScreen(
                            notes = notesByBookId.value,
                            onNoteClick = { note ->
                                editingContent = note.content
                                currentEditingNoteId = note.id
                                showDialog = true
                            },
                            onDeleteClick = { note ->
                                viewModel.deleteNote(note)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesListScreen(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
) {
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val elderlyMode = LocalElderlyMode.current
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    val isElderlyModeEnabled =
        remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    CompositionLocalProvider(LocalElderlyMode provides isElderlyModeEnabled) {
        BookRecordTheme(themeColor) {
            if (elderlyMode.value) {
                LazyColumn {
                    items(items = notes, key = { it.id }) { note ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = note.content,
                                    fontSize = 24.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNoteClick(note) }
                                )
                                IconButton(onClick = { onDeleteClick(note) }) { // 当点击删除按钮时，调用 onDeleteClick
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else {
                LazyColumn {
                    items(items = notes, key = { it.id }) { note ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = note.content,
                                    fontSize = 18.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNoteClick(note) }
                                )
                                IconButton(onClick = { onDeleteClick(note) }) { // 当点击删除按钮时，调用 onDeleteClick
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
