package com.example.BookRecord

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController


@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
){
    // 鼠标的焦点
    val focusManager = LocalFocusManager.current
    // 搜索文本状态
    var searchText by remember { mutableStateOf("") }

    val bookViewModel = LocalBooksViewModel.current

    // 获取正在读的书籍列表
    val readingBooks = bookViewModel.getReadingBooks()

    // 搜索框和列表布局
    // 使得 Column 可点击，并在点击时清除焦点
    Scaffold(
        floatingActionButton = {
            SmallAddButton(onClick = {
                navController.navigate("AddBooks")
            // 在这里定义点击悬浮按钮后的动作，比如打开添加图书的界面
            })
        },
        floatingActionButtonPosition = FabPosition.End, // 将按钮放在右下角
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .clickable(onClick = { focusManager.clearFocus() })
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically // 垂直居中对齐 Row 内的元素
            ) {
                Column(modifier = Modifier.weight(9f)){
                    Text(
                        text = "Books you are reading",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6650a4),
                        modifier = Modifier.padding(end = 5.dp) // 根据需要调整文本的右边距
                    )
                }
                Column(modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally){
                    // 使用一个图标，表示设置
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFF6650a4),
                        modifier = Modifier.size(40.dp) // 根据需要调整图标的大小
                    )
                }
            }

            // 搜索框
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Filter book", color = Color(0xFF6650a4)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(20.dp)), // 设置浅灰色背景和圆角形状
                shape = RoundedCornerShape(20.dp), // 设置输入框的四个角更圆滑
                trailingIcon = { // 在搜索框的右侧添加一个搜索图标
                    Icon(
                        imageVector = Icons.Filled.FilterAlt,
                        contentDescription = "Search",
                        modifier = Modifier.clickable { focusManager.clearFocus() }
                    )
                },

                )

            // 基于搜索文本过滤整个书籍列表，而不仅仅是标题
            val filteredBooks = readingBooks.filter { it.title.contains(searchText, ignoreCase = true) }

            // 将过滤后的书籍列表传递给 Books 函数
            Books(navController, books = filteredBooks,bookViewModel = bookViewModel)
        }
    }

}



@Composable
fun Books(navController: NavController, books: List<Book>, modifier: Modifier = Modifier,bookViewModel:BookViewModel) {
    val context = LocalContext.current
    LazyColumn {
        items(books) { book -> // 正确使用 books 作为列表
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ){
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(), // 确保 Row 填充父容器宽度

                ) {
                    // 用 Image 替换原有的 Column 显示书名
                    Box(modifier = Modifier
                        ){
                        Image(
                            // 把图片替换成 Book.image
                            painter = painterResource(id = R.drawable.book1), // 确保你的图片资源正确
                            contentDescription = "Book Image",
                            modifier = Modifier
                                .size(100.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Row(modifier = Modifier
                            .padding(top = 0.dp, start = 15.dp)
                        ){
                            Text(text = book.title, fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 40.dp, start = 15.dp)
                        ) {
                            // 这个column 是一个 Expanded Dropdown Menu
                            Column(
                                modifier = Modifier
                                    .weight(0.5f)
                            ){
                                ExpandedDropdownMenuExample(bookViewModel= bookViewModel, book=book)
                            }
                            // 这个column 是一个方形的 button
                            Column(
                                modifier = Modifier
                                    .weight(0.5f)
                            ){
                                OutlinedButton(
                                    modifier = Modifier
                                        .height(30.dp)
                                        .width(100.dp),
                                    contentPadding = PaddingValues(),
                                    shape = RoundedCornerShape(5.dp),
                                    onClick ={ navController.navigate("EditNotesScreen")},
                                ) {
                                    Text(text = "notes", fontSize = 15.sp)
                                    Icon(
                                        imageVector = Icons.Filled.EditNote,
                                        contentDescription = "Select",
                                        tint = Color(0xFF6650a4),
                                        modifier = Modifier
                                            .size(25.dp)
                                            .padding(start = 5.dp, top = 5.dp)
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier
                            .padding(start = 15.dp,top = 72.dp)
                        ){
                            Column(
                                modifier = Modifier.weight(3.5f)
                            ) {
                                Text(text = "From", fontSize = 12.sp)
                                Text(text = "2024/03/08", fontSize = 12.sp)

                            }
                            Column(
                                modifier = Modifier
                                    .weight(2.5f)
                            ) {
                                Text(text = "7", fontSize = 12.sp)
                                Text(text = "days", fontSize = 12.sp)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(4f)
                            ){
                                Row(){
                                    InputPageNumberDialogButton(bookViewModel=bookViewModel,book = book)
                                    Box(modifier = Modifier.padding(top = 13.dp,start = 2.dp)){
                                        Text(text = "/${book.pages}", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun InputPageNumberDialogButton(bookViewModel: BookViewModel, book: Book) {
    var showDialog by remember { mutableStateOf(false) }
    var pageNumber by remember { mutableStateOf(book.read_page.toString()) }

    // Button to show dialog
    Box(modifier = Modifier.padding(top = 5.dp)) {
        OutlinedButton(
            modifier = Modifier
                .height(25.dp)
                .width(50.dp),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(5.dp),
            onClick = {
                showDialog = true // Show dialog when button is clicked
            },
        ) {
            Text(text = pageNumber, fontSize = 15.sp)
        }
    }

    // Dialog for input
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            // Dialog content
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Enter the page number", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pageNumber,
                        onValueChange = { value ->
                            // Update only if the input is numeric
                            if (value.all { it.isDigit() }) {
                                pageNumber = value
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            pageNumber.toIntOrNull()?.let {
                                // Here you update the read_page of the book
                                bookViewModel.updateBookReadPage(book.id, it)
                            }
                            showDialog = false // Hide dialog when done
                        }
                    ) {
                        Text("Done", fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SmallAddButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier.padding( bottom = 35.dp)
    ) {
        Icon(Icons.Filled.Add, "Small floating action button.")
    }
}

@Composable
fun ExpandedDropdownMenuExample(bookViewModel: BookViewModel, book: Book) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("reading", "have read","lay aside")
    var selectedIndex by remember { mutableStateOf(0) }
    // 为每个菜单项定义一个图标，与菜单项的标题对应
    val icons = listOf(Icons.Filled.MenuBook, Icons.Filled.Done,Icons.Filled.DeleteForever,)


    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            modifier = Modifier
                .height(30.dp)
                .width(100.dp),
            contentPadding = PaddingValues(start = 5.dp),
            shape = RoundedCornerShape(5.dp),
            onClick = { expanded = true } ,
        ) {
            Icon(
                imageVector = Icons.Filled.MenuBook,
                contentDescription = "Select",
                tint = Color(0xFF6650a4),
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 3.dp)
            )
            Text(text = items[selectedIndex], fontSize = 15.sp)
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Select",
                tint = Color(0xFF6650a4),
                modifier = Modifier
                    .size(20.dp)
            )
        }

        DropdownMenu(
            modifier = Modifier, // 可以调整宽度以适应内容
            expanded = expanded,
            onDismissRequest = { expanded = false },

            ) {
            items.forEachIndexed { index, title ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        // 根据选择的项更新书籍状态
                        when (index) {
                            1 -> bookViewModel.updateBookStatus(book.id, BookStatus.READ) // "have read"
                            2 -> bookViewModel.updateBookStatus(book.id, BookStatus.ON_HOLD) // "lay aside"
                        }
                    },
                    leadingIcon = {
                        Icon(icons[index], contentDescription = null) // 为每个选项添加对应的图标
                    }
                )
            }
        }
    }
}

