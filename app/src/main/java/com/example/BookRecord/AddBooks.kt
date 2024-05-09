package com.example.BookRecord

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Insets.add
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.BookRecord.ui.theme.BookRecordTheme
import com.example.BookRecord.ui.theme.ThemeManager
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    bookViewModel: BookViewModel = viewModel() // 使用 viewModel() 获取 ViewModel 的实例
) {

    // The focus manager to handle the keyboard actions
    val focusManager = LocalFocusManager.current

    // State for search text
    var searchText by remember { mutableStateOf("") }
    val searchResults by bookViewModel.searchResults.observeAsState(initial = emptyList()) // 观察 LiveData 并将其状态保存

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
                Column(
                    modifier = modifier
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back icon with a larger touch target for better accessibility
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(35.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp)) // Add space between the icon and the search bar

                        // Search input field
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            label = {
                                Text(
                                    "Search by title or author",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = TextStyle(fontSize = 22.sp) // 调整文本大小
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFF2F2F2),
                                    RoundedCornerShape(20.dp)
                                ), // 直接在这里设置背景颜色和形状
                            shape = RoundedCornerShape(20.dp), // 设置输入框的形状
                            colors = TextFieldDefaults.outlinedTextFieldColors(
//                    backgroundColor = Color(0xFFF2F2F2),
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 20.sp), // 调整输入文字大小
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(onClick = { searchText = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus() // Hide the keyboard
                                    // TODO: Implement the search logic here
                                    bookViewModel.searchBooks(searchText) // 调用 ViewModel 中的搜索函数

                                }
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                        )
                    }

                    // TODO: Add the rest of your UI components here
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn {
                        items(searchResults) { book ->
                            NetworkBookItem(book = book)
                        }
                    }
                }
            }
            else {
                Column(
                    modifier = modifier
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back icon with a larger touch target for better accessibility
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(35.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp)) // Add space between the icon and the search bar

                        // Search input field
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            label = {
                                Text(
                                    "Search by title or author",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFF2F2F2),
                                    RoundedCornerShape(20.dp)
                                ), // 直接在这里设置背景颜色和形状
                            shape = RoundedCornerShape(20.dp), // 设置输入框的形状
                            colors = TextFieldDefaults.outlinedTextFieldColors(
//                    backgroundColor = Color(0xFFF2F2F2),
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            singleLine = true,
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(onClick = { searchText = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus() // Hide the keyboard
                                    // TODO: Implement the search logic here
                                    bookViewModel.searchBooks(searchText) // 调用 ViewModel 中的搜索函数

                                }
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                        )
                    }

                    // TODO: Add the rest of your UI components here
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn {
                        items(searchResults) { book ->
                            NetworkBookItem(book = book)
                        }
                    }
                }
            }

    }
}

@Composable
fun NetworkBookItem(book: Book) {
    var showDialog by remember { mutableStateOf(false) }  // 用于控制对话框显示的状态
    val bookViewModel: BookViewModel = viewModel() // 使用 viewModel() 获取 ViewModel 的实例
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
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            // 当用户尝试通过点击外部区域取消对话框时调用
                            showDialog = false
                        },
                        title = {
                            Text(text = "Add Book", fontSize = 30.sp,
                                color = Color.DarkGray)
                        },
                        text = {
                            Text("Do you want to add '${book.title}' to your bookshelf?",
                                 fontSize = 20.sp,
                                 color = Color.DarkGray,
                                 style = TextStyle(
                                    lineHeight = 30.sp // 设置为1.5倍行距
                                 )
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    // 在这里添加将书籍加入书架的逻辑
                                    // 使用 book 对象的数据调用 ViewModel 中的 addBook 方法

                                    bookViewModel.addBook(
                                        bookTitle = book.title,
                                        bookImage = book.image,
                                        author = book.author,
                                        pages = book.pages,
                                        status = book.status,
                                        readPage = book.readpage,
                                        press = book.press,
                                        startTime = book.startTime
                                    )


                                }
                            ) {
                                Text("YES", fontSize = 20.sp)
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text(" NO ", fontSize = 20.sp)
                            }
                        }
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Image Box
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .padding(end = 10.dp)  // Add spacing between the image and text
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.book1),
                                    contentDescription = "Book Cover",
                                    modifier = Modifier.fillMaxSize()
                                        .size(130.dp)
                                )
                            }

                            // Text Box
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)  // Align the text to the center of the image
                            ) {
                                Text(
                                    text = "Title: ${book.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = "Author: ${book.author}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Press: ${book.press}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Pages: ${book.pages}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        // Add button placed in the top-right corner of the Card
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp) // Add some padding to the button
                        ) {
                            IconButton(
                                onClick = { showDialog = true },
                                modifier = Modifier
                                    .size(20.dp)  // Standard touch target size
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)  // Adjust color to fit your theme
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Book",
                                    tint = Color.White  // Adjust icon color to improve visibility
                                )
                            }
                        }
                    }
                }
            }
            else{
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            // 当用户尝试通过点击外部区域取消对话框时调用
                            showDialog = false
                        },
                        title = {
                            Text(text = "Add Book",
                                color = Color.DarkGray)
                        },
                        text = {
                            Text("Do you want to add '${book.title}' to your bookshelf?",
                                color = Color.DarkGray)
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    // 在这里添加将书籍加入书架的逻辑
                                    // 使用 book 对象的数据调用 ViewModel 中的 addBook 方法

                                    bookViewModel.addBook(
                                        bookTitle = book.title,
                                        bookImage = book.image,
                                        author = book.author,
                                        pages = book.pages,
                                        status = book.status,
                                        readPage = book.readpage,
                                        press = book.press,
                                        startTime = book.startTime
                                    )


                                }
                            ) {
                                Text("YES")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("NO")
                            }
                        }
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        // Image Box
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 10.dp)  // Add spacing between the image and text
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.book1),
                                contentDescription = "Book Cover",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Text Box
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)  // Align the text to the center of the image
                        ) {
                            Text(
                                text = "Title: ${book.title}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Author: ${book.author}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Pages: ${book.pages}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Press: ${book.press}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }

                        // Add button placed in the bottom-right corner of the Card
                        Column(
                            verticalArrangement = Arrangement.Bottom, // 垂直方向上靠下对齐
                            horizontalAlignment = Alignment.End // 水平方向上靠右对齐
                        ) {
                            IconButton(
                                onClick = { showDialog = true },
                                modifier = Modifier
                                    .size(20.dp)  // Standard touch target size
                                    // .clip(CircleShape)  // Ensures the button is circular
                                    .background(MaterialTheme.colorScheme.primary)  // Adjust color to fit your theme
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Book",
                                    tint = Color.White  // Adjust icon color to improve visibility
                                )
                            }
                        }
                    }
                }
            }
        }
    }





