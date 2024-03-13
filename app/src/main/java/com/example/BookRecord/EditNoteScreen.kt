package com.example.BookRecord

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// 定义一个笔记的数据类
data class Note(val id: Int, var content: String)


@Composable
fun EditNotesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
){
    var showDialog by remember { mutableStateOf(false) }
    // 编辑笔记的内容
    var editingContent by remember { mutableStateOf("") }
    // 保存笔记列表
    var notes by remember { mutableStateOf(listOf<Note>()) }
    // 当前正在编辑的笔记
    var currentEditingNoteId by remember { mutableStateOf<Int?>(null) }
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
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically // 垂直居中对齐 Row 内的元素
            ) {
                Column(modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally){

                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6650a4),
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                navController.navigate("Book")
                            }
                    )
                }

                Column(modifier = Modifier.weight(9f)) {
                    Text(
                        text = "Edit Notes",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6650a4),
                        modifier = Modifier.padding(start = 20.dp) // 根据需要调整文本的右边距
                    )
                }
            }

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
                                    notes = notes + Note(id = notes.size + 1, content = editingContent)
                                } else {
                                    // 更新现有笔记
                                    notes = notes.map { note ->
                                        if (note.id == currentEditingNoteId) note.copy(content = editingContent) else note
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

            // 笔记列表
            Notes(names = notes) { note ->
                // 设置编辑状态并显示编辑笔记对话框
                editingContent = note.content
                currentEditingNoteId = note.id
                showDialog = true
            }
        }
    }

}



@Composable
fun Notes(names: List<Note>, onNoteClick: (Note) -> Unit) {
    LazyColumn {
        items(names, key = { it.id }) { note ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .clickable { onNoteClick(note) },
            ){
                Text(
                    text = note.content,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}