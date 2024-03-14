package com.example.BookRecord

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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




@Composable
fun NotesScreen( // 重命名为 NotesScreen
    navController: NavController, modifier: Modifier = Modifier,
) {
    // 示例笔记数据
    val exampleNotes = listOf(
        "Finished reading '1984'. Remarkable insights into authoritarianism.",
        "Started 'The Catcher in the Rye'. The narrative style is unique.",
        "Need to buy 'To Kill a Mockingbird'. It comes highly recommended."
    )

    Scaffold() {paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(0.dp),
                verticalAlignment = Alignment.CenterVertically // 垂直居中对齐 Row 内的元素
            ){
                Column(modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally){

                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6650a4),
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                navController.navigate("Bookshelf")
                            }
                    )
                }

                Column(modifier = Modifier.weight(9f)) {
                    Text(
                        text = "View Notes",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6650a4),
                        modifier = Modifier.padding(start = 20.dp) // 根据需要调整文本的右边距
                    )
                }
            }

            // 笔记列表
            NoteList(notes = exampleNotes, modifier = modifier.padding(paddingValues).padding(16.dp))
        }
    }
}

@Composable
fun NoteList(notes: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(items = notes, key = { note -> note.hashCode() }) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = note)
                }
            }
        }
    }
}


