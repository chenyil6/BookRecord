package com.example.BookRecord

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.BookRecord.ui.theme.BookRecordTheme
import com.example.BookRecord.ui.theme.ThemeManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

@Composable
fun HomeScreen(
    googleSignInClient: GoogleSignInClient,
    navController: NavController,
    //auth: FirebaseAuth, // 传入FirebaseAuth实例
    modifier: Modifier = Modifier,
) {
    // 鼠标的焦点
    val focusManager = LocalFocusManager.current
    // 搜索文本状态
    var searchText by remember { mutableStateOf("") }
    val elderlyMode = LocalElderlyMode.current
    val bookViewModel = LocalBooksViewModel.current
    val readingRecordViewModel = LocalreadingRecordViewModel.current
    // 获取正在读的书籍列表
    val readingBooks by bookViewModel.readingBooks.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
// 使用remember保存老年模式的状态，并使用DisposableEffect监听SharedPreferences的变更
    var isElderlyModeEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "elderly_mode_enabled") {
                isElderlyModeEnabled = sharedPreferences.getBoolean(key, false)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
        BookRecordTheme(themeColor) {

            // 搜索框和列表布局
            // 使得 Column 可点击，并在点击时清除焦点
            Scaffold(
                floatingActionButton = {
                    SmallAddButton(onClick = {
                        // 在这里定义点击悬浮按钮后的动作，比如打开添加图书的界面
                        navController.navigate("AddBooks")

                    })
                },
                floatingActionButtonPosition = FabPosition.End, // 将按钮放在右下角
            ) { paddingValues ->

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .clickable(onClick = { focusManager.clearFocus() })
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐 Row 内的元素
                    ) {
                        Column(modifier = Modifier.weight(9f)) {
                            Text(
                                text = "Books you are reading",
                                fontSize = if (isElderlyModeEnabled) 28.sp else 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 5.dp) // 根据需要调整文本的右边距
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f).clickable {
                                navController.navigate("SettingScreen")
                            },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 使用一个图标，表示设置
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(if (isElderlyModeEnabled) 50.dp else 40.dp) // 根据需要调整图标的大小
                            )
                        }
                    }

                    // 搜索框
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = {
                            Text(
                                "Filter book",
                                color = MaterialTheme.colorScheme.primary,
                                style = TextStyle(fontSize = if (isElderlyModeEnabled) 20.sp else 16.sp)
                            ) // 调整文本大小)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                            .background(
                                Color(0xFFF2F2F2),
                                RoundedCornerShape(20.dp)
                            ), // 设置浅灰色背景和圆角形状
                        shape = RoundedCornerShape(20.dp), // 设置输入框的四个角更圆滑
                        trailingIcon = { // 在搜索框的右侧添加一个搜索图标
                            Icon(
                                imageVector = Icons.Filled.FilterAlt,
                                contentDescription = "Search",
                                modifier = Modifier.clickable { focusManager.clearFocus() }
                            )
                        },
                        textStyle = TextStyle(fontSize = if (isElderlyModeEnabled) 20.sp else 14.sp), // 调整输入文字大小
                    )

                    // 基于搜索文本过滤整个书籍列表，而不仅仅是标题
                    val filteredBooks =
                        readingBooks.filter { it.title.contains(searchText, ignoreCase = true) }

                    // 将过滤后的书籍列表传递给 Books 函数
                    Books(
                        navController,
                        books = filteredBooks,
                        bookViewModel = bookViewModel,
                        readingRecordViewModel = readingRecordViewModel
                    )
                }
            }
        }
    }



@Composable
fun Books(navController: NavController, books: List<Book>, modifier: Modifier = Modifier,bookViewModel:BookViewModel,readingRecordViewModel:ReadingRecordViewModel) {
    val elderlyMode = LocalElderlyMode.current
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
// 使用remember保存老年模式的状态，并使用DisposableEffect监听SharedPreferences的变更
    var isElderlyModeEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "elderly_mode_enabled") {
                isElderlyModeEnabled = sharedPreferences.getBoolean(key, false)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    BookRecordTheme(themeColor) {
            LazyColumn {
                items(books) { book -> // 正确使用 books 作为列表
                    if (isElderlyModeEnabled) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(), // 确保 Row 填充父容器宽度
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 用 Image 替换原有的 Column 显示书名
                                    Box(
                                        modifier = Modifier
                                    ) {
                                        Image(
                                            // 把图片替换成 Book.image
                                            painter = painterResource(id = R.drawable.book1), // 确保你的图片资源正确
                                            contentDescription = "Book Image",
                                            modifier = Modifier
                                                .size(150.dp)

                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier
                                                .padding(top = 0.dp, start = 15.dp)
                                        ) {
                                            Text(
                                                text = book.title,
                                                fontSize = 18.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                        Row(
                                            modifier = Modifier
                                                .padding(top = 5.dp, start = 15.dp)
                                        ) {
                                            // 这个column 是一个 Expanded Dropdown Menu
                                            Column(
                                                modifier = Modifier
                                                    .weight(0.5f)
                                            ) {
                                                ExpandedDropdownMenuExample(
                                                    bookViewModel = bookViewModel,
                                                    book = book
                                                )
                                            }
                                            // 这个column 是一个方形的 button
                                        }
                                        Row(
                                            modifier = Modifier
                                                .padding(top = 10.dp, start = 15.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(0.5f)
                                            ) {
                                                OutlinedButton(
                                                    modifier = Modifier
                                                        .height(35.dp)
                                                        .width(150.dp),
                                                    contentPadding = PaddingValues(),
                                                    shape = RoundedCornerShape(5.dp),
                                                    onClick = { navController.navigate("EditNotesScreen/${book.id}") }
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically, // 垂直居中对齐
                                                        horizontalArrangement = Arrangement.Start // 水平开始对齐
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Filled.EditNote,
                                                            contentDescription = "Select",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier
                                                                .size(30.dp) // 调整图标大小以适应按钮高度
                                                                .weight(0.5f) // 图标不占用额外空间，固定在左侧
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp)) // 在图标和文本之间添加一些间隔
                                                        Text(
                                                            text = "notes",
                                                            fontSize = 20.sp,
                                                            modifier = Modifier.weight(1f) // 文本占用剩余空间
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "From",
                                            fontSize = 18.sp,
                                            color = Color.DarkGray
                                        )
                                        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                                        val dateString = book.startTime.format(formatter)
                                        Text(
                                            text = dateString,
                                            fontSize = 18.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(15.dp))
                                    // 已经读了多少天
                                    Column(modifier = Modifier.weight(1f)) {
                                        // 计算从开始阅读到现在的天数
                                        val daysRead =
                                            ChronoUnit.DAYS.between(book.startTime, LocalDate.now())
                                                .toInt()
                                        Text(
                                            text = daysRead.toString(),
                                            fontSize = 18.sp,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = "days",
                                            fontSize = 18.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row() {
                                            InputPageNumberDialogButton(
                                                bookViewModel = bookViewModel,
                                                book = book,
                                                readingRecordViewModel = readingRecordViewModel
                                            )
                                            Box(
                                                modifier = Modifier.padding(
                                                    top = 13.dp,
                                                    start = 2.dp
                                                )
                                            ) {
                                                Text(
                                                    text = "/${book.pages}",
                                                    fontSize = 18.sp,
                                                    color = Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                            }

                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(), // 确保 Row 填充父容器宽度

                            ) {
                                // 用 Image 替换原有的 Column 显示书名
                                Box(
                                    modifier = Modifier
                                ) {
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
                                    Row(
                                        modifier = Modifier
                                            .padding(top = 0.dp, start = 15.dp)
                                    ) {
                                        Text(text = book.title, fontSize = 13.sp, color = Color.DarkGray)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .padding(top = 40.dp, start = 15.dp)
                                    ) {
                                        // 这个column 是一个 Expanded Dropdown Menu
                                        Column(
                                            modifier = Modifier
                                                .weight(0.5f)
                                        ) {
                                            ExpandedDropdownMenuExample(
                                                bookViewModel = bookViewModel,
                                                book = book
                                            )
                                        }
                                        // 这个column 是一个方形的 button
                                        Column(
                                            modifier = Modifier
                                                .weight(0.5f)
                                        ) {
                                            OutlinedButton(
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .width(100.dp),
                                                contentPadding = PaddingValues(),
                                                shape = RoundedCornerShape(5.dp),
                                                onClick = { navController.navigate("EditNotesScreen/${book.id}") },
                                                //navController.navigate("EditNotesScreen/${book.id}")
                                            ) {
                                                Text(text = "notes", fontSize = 15.sp)
                                                Icon(
                                                    imageVector = Icons.Filled.EditNote,
                                                    contentDescription = "Select",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier
                                                        .size(25.dp)
                                                        .padding(start = 5.dp, top = 5.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .padding(start = 15.dp, top = 72.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(3.5f)
                                        ) {
                                            Text(text = "From", fontSize = 12.sp, color = Color.DarkGray)
                                            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                                            val dateString = book.startTime.format(formatter)
                                            Text(text = dateString, fontSize = 12.sp, color = Color.DarkGray)
                                        }
                                        // 已经读了多少天
                                        Column(
                                            modifier = Modifier
                                                .weight(2.5f)
                                        ) {
                                            // 计算从开始阅读到现在的天数
                                            val daysRead =
                                                ChronoUnit.DAYS.between(book.startTime, LocalDate.now())
                                                    .toInt()
                                            Text(text = daysRead.toString(), fontSize = 12.sp, color = Color.DarkGray)
                                            Text(text = "days", fontSize = 12.sp, color = Color.DarkGray)
                                        }

                                        Column(
                                            modifier = Modifier
                                                .weight(4f)
                                        ) {
                                            Row() {
                                                InputPageNumberDialogButton(
                                                    bookViewModel = bookViewModel,
                                                    book = book,
                                                    readingRecordViewModel = readingRecordViewModel
                                                )
                                                Box(
                                                    modifier = Modifier.padding(
                                                        top = 13.dp,
                                                        start = 2.dp
                                                    )
                                                ) {
                                                    Text(text = "/${book.pages}", fontSize = 12.sp, color = Color.DarkGray)
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
        }
    }


@Composable
fun InputPageNumberDialogButton(bookViewModel: BookViewModel, book: Book,readingRecordViewModel: ReadingRecordViewModel ) {
    var showDialog by remember { mutableStateOf(false) }
    var pageNumber by remember { mutableStateOf(book.readpage.toString()) }
    val elderlyMode = LocalElderlyMode.current
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
// 使用remember保存老年模式的状态，并使用DisposableEffect监听SharedPreferences的变更
    var isElderlyModeEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "elderly_mode_enabled") {
                isElderlyModeEnabled = sharedPreferences.getBoolean(key, false)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
        BookRecordTheme(themeColor) {
            if (isElderlyModeEnabled) {// Button to show dialog
                Box(modifier = Modifier.padding(top = 10.dp)) {
                    OutlinedButton(
                        modifier = Modifier
                            .height(30.dp)
                            .width(55.dp),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            showDialog = true // Show dialog when button is clicked
                        },
                    ) {
                        Text(text = pageNumber, fontSize = 18.sp)
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
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Enter the page number",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = pageNumber,
                                    onValueChange = { value ->
                                        // Update only if the input is numeric
                                        if (value.all { it.isDigit() }) {
                                            pageNumber = value
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val oldPageNumber = book.readpage.toIntOrNull() ?: 0
                                        pageNumber.toIntOrNull()?.let { newPageNumber ->
                                            book.readpage = newPageNumber.toString()
                                            bookViewModel.updateBookReadPage(
                                                book,
                                                newPageNumber.toString()
                                            )
                                            val pagesToAdd = newPageNumber - oldPageNumber
                                            if (pagesToAdd > 0) {
                                                val newReadingRecord = ReadingRecord(
                                                    userId = book.userId,
                                                    date = LocalDate.now(),
                                                    readPages = pagesToAdd
                                                )
                                                readingRecordViewModel.insertReadingRecord(
                                                    newReadingRecord
                                                )
                                            }
                                            showDialog = false
                                        }
                                    }
                                ) {
                                    Text("Done", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

        }else{
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
                                        val oldPageNumber = book.readpage.toIntOrNull() ?: 0
                                        pageNumber.toIntOrNull()?.let { newPageNumber ->
                                            book.readpage = newPageNumber.toString()
                                            bookViewModel.updateBookReadPage(book, newPageNumber.toString())
                                            val pagesToAdd = newPageNumber - oldPageNumber
                                            if (pagesToAdd > 0) {
                                                val newReadingRecord = ReadingRecord(
                                                    userId = book.userId,
                                                    date = LocalDate.now(),
                                                    readPages = pagesToAdd
                                                )
                                                readingRecordViewModel.insertReadingRecord(newReadingRecord)
                                            }
                                            showDialog = false
                                        }
                                    }
                                ) {
                                    Text("Done", fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun SmallAddButton(onClick: () -> Unit) {
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    val isElderlyModeEnabled =
        remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    CompositionLocalProvider(LocalElderlyMode provides isElderlyModeEnabled) {
        BookRecordTheme(themeColor) {
            FloatingActionButton(
                onClick = { onClick() },
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 35.dp)
            ) {
                Icon(Icons.Filled.Add, "Small floating action button.")
            }
        }
    }
}

@Composable
fun ExpandedDropdownMenuExample(bookViewModel: BookViewModel, book: Book) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Reading", "Complete", "Lay Aside") // 匹配枚举的描述
    val statuses = listOf(BookStatus.READING, BookStatus.READ, BookStatus.ON_HOLD)
    var selectedIndex by remember { mutableStateOf(statuses.indexOf(book.status)) } // 初始选择基于书籍的当前状态
    val icons =
        listOf(Icons.Filled.MenuBook, Icons.Filled.Done, Icons.Filled.DeleteForever) // 对应状态的图标
    val context = LocalContext.current
    val elderlyMode = LocalElderlyMode.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
// 使用remember保存老年模式的状态，并使用DisposableEffect监听SharedPreferences的变更
    var isElderlyModeEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("elderly_mode_enabled", false)) }
    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "elderly_mode_enabled") {
                isElderlyModeEnabled = sharedPreferences.getBoolean(key, false)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    BookRecordTheme(themeColor) {
            if (isElderlyModeEnabled) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        modifier = Modifier
                            .height(35.dp)
                            .width(150.dp),
                        contentPadding = PaddingValues(start = 5.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = { expanded = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, // 垂直居中对齐
                            horizontalArrangement = Arrangement.Start // 水平开始对齐
                        ) {
                            Icon(
                                imageVector = icons[selectedIndex], // 显示当前选择的状态图标
                                contentDescription = "Select",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .weight(0.5f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = items[selectedIndex],
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Select",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        items.forEachIndexed { index, title ->
                            DropdownMenuItem(
                                text = { Text(title, fontSize = 18.sp)},
                                onClick = {
                                    selectedIndex = index
                                    expanded = false
                                    // 更新书籍状态
                                    bookViewModel.updateBookStatus(book, statuses[index])
                                },
                                leadingIcon = {
                                    Icon(
                                        icons[index],
                                        contentDescription = null
                                    )
                                } // 为每个选项添加对应的图标
                            )
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        modifier = Modifier
                            .height(30.dp)
                            .width(100.dp),
                        contentPadding = PaddingValues(start = 5.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = { expanded = true },
                    ) {
                        Icon(
                            imageVector = icons[selectedIndex], // 显示当前选择的状态图标
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 3.dp)
                        )
                        Text(text = items[selectedIndex], fontSize = 15.sp)
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        items.forEachIndexed { index, title ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                onClick = {
                                    selectedIndex = index
                                    expanded = false
                                    // 更新书籍状态
                                    bookViewModel.updateBookStatus(book, statuses[index])
                                },
                                leadingIcon = { Icon(icons[index], contentDescription = null) } // 为每个选项添加对应的图标
                            )
                        }
                    }
                }
            }

    }
}

