package com.example.BookRecord

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.navigation.NavController
import com.example.BookRecord.ui.theme.BookRecordTheme
import com.example.BookRecord.ui.theme.ThemeManager


@Composable
fun BookShelf(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    // 鼠标的焦点
    val focusManager = LocalFocusManager.current
    // 搜索文本状态
    var searchText by remember { mutableStateOf("") }

    //tab的标签
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("complete", "lay aside")

    val bookViewModel = LocalBooksViewModel.current

    // 获取读完的书籍列表
    val completeBooks by bookViewModel.completeBooks.observeAsState(initial = emptyList())
    // 获取搁置的书籍列表
    val layasideBooks by bookViewModel.layasideBooks.observeAsState(initial = emptyList())

    // 根据选中的Tab来决定展示哪个列表
    val booksToShow = if (selectedTabIndex == 0) completeBooks else layasideBooks
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val elderlyMode = LocalElderlyMode.current
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
            if (isElderlyModeEnabled) {
                Column(
                    modifier = modifier
                        .clickable(onClick = { focusManager.clearFocus() })
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐 Row 内的元素
                    ) {
                        Column(modifier = Modifier.weight(9f)) {
                            Text(
                                text = "Bookshelf",
                                fontSize = 28.sp,
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
                                contentDescription = "Setting",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp) // 根据需要调整图标的大小
                            )
                        }
                    }
                    // 搜索框
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Filter book",
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontSize = 20.sp)
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
                        textStyle = TextStyle(fontSize = 20.sp), // 调整输入文字大小
                    )

                    // Tab 行
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 0.dp,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = {},
                        divider = {}) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        title,
                                        fontSize = 20.sp,
                                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                })
                        }
                    }

                    // 图书网格列表
                    BookGrid(
                        navController,
                        viewModel = bookViewModel,
                        books = booksToShow.filter {
                            it.title.contains(
                                searchText,
                                ignoreCase = true
                            )
                        })
                }
            }
            else{
                Column(
                    modifier = modifier
                        .clickable(onClick = { focusManager.clearFocus() })
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐 Row 内的元素
                    ) {
                        Column(modifier = Modifier.weight(9f)) {
                            Text(
                                text = "Bookshelf",
                                fontSize = 25.sp,
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
                                contentDescription = "Setting",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp) // 根据需要调整图标的大小
                            )
                        }
                    }
                    // 搜索框
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Filter book", color = MaterialTheme.colorScheme.primary) },
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

                    // Tab 行
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 0.dp,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = {},
                        divider = {}) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        title,
                                        fontSize = 16.sp,
                                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                })
                        }
                    }

                    // 图书网格列表
                    BookGrid(
                        navController,
                        viewModel = bookViewModel,
                        books = booksToShow.filter { it.title.contains(searchText, ignoreCase = true) })
                }
            }

    }
}

@Composable
fun BookGrid(navController:NavController,viewModel: BookViewModel,books: List<Book>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val themeColor = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }
    val elderlyMode = LocalElderlyMode.current
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
                val columns = 2 // 定义每行显示的书籍数量
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = modifier.padding(4.dp)
                ) {
                    items(books) { book ->
                        val noteCount =
                            viewModel.getNoteCountByBookId(book.id).observeAsState(initial = 0)
                        Card(
                            modifier = Modifier
                                .aspectRatio(3f / 4f), // 假设每本书的尺寸比例为3:4
                            colors = CardDefaults.cardColors(containerColor = Color.White) // 设置卡片背景色为白色
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.book1), // 你的图片资源
                                    contentDescription = "Book Image",
                                    modifier = Modifier
                                        .fillMaxSize() // 填满 Card
                                        .clickable {
                                            navController.navigate("notesScreen/${book.id}")
                                        }
                                )
                                // 笔记图标和数量显示在右下角
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Note Icon",
                                        tint = Color.Gray, // 图标颜色，根据需要调整
                                        modifier = Modifier.size(30.dp)
                                    )
                                    // 假设笔记数量为示例用，你可以根据实际情况动态设置
                                    Text(
                                        text = "${noteCount.value}",
                                        color = Color.Gray,
                                        fontSize = 20.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else{
                val columns = 3 // 定义每行显示的书籍数量
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = modifier.padding(4.dp)
                ) {
                    items(books) { book ->
                        val noteCount =
                            viewModel.getNoteCountByBookId(book.id).observeAsState(initial = 0)
                        Card(
                            modifier = Modifier
                                .aspectRatio(3f / 4f), // 假设每本书的尺寸比例为3:4
                            colors = CardDefaults.cardColors(containerColor = Color.White) // 设置卡片背景色为白色
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.book1), // 你的图片资源
                                    contentDescription = "Book Image",
                                    modifier = Modifier
                                        .fillMaxSize() // 填满 Card
                                        .clickable {
                                            navController.navigate("notesScreen/${book.id}")
                                        }
                                )
                                // 笔记图标和数量显示在右下角
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Note Icon",
                                        tint = Color.Gray, // 图标颜色，根据需要调整
                                        modifier = Modifier.size(20.dp)
                                    )
                                    // 假设笔记数量为示例用，你可以根据实际情况动态设置
                                    Text(
                                        text = "${noteCount.value}",
                                        color = Color.Gray,
                                        fontSize = 15.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }

    }
}