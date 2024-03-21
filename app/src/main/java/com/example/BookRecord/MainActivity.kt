package com.example.BookRecord

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.BookRecord.ui.theme.AnalyticsScreen
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val notesViewModel: NoteViewModel by viewModels()
            val bookViewModel: BookViewModel by viewModels()
            // 使用CompositionLocalProvider提供ViewModel，使用不同的键
            CompositionLocalProvider(
                LocalNotesViewModel provides notesViewModel,
                LocalBooksViewModel provides bookViewModel
            ) {
                AppNavigation()
            }
        }

    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // 根据当前的导航目的地决定是否显示底部导航栏
    val shouldShowBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route !in listOf("notesScreen","EditNotesScreen","LoginScreen","AddBooks")
    Scaffold(
        bottomBar = { if (shouldShowBottomBar) {
                        BottomNavigationBar(navController) }
                    }
    ) { innerPadding ->
        // 利用提供的 innerPadding 参数调整内容的内边距
        NavHost(
            navController = navController,
            startDestination = "LoginScreen",
            modifier = Modifier.padding(innerPadding) // 应用内边距
        ) {
            composable("LoginScreen") {
                LoginScreen(navController, modifier = Modifier.fillMaxSize())
            }
            composable("Book") { HomeScreen(navController,modifier = Modifier.fillMaxSize())}
            composable("Bookshelf"){BookShelf(navController,modifier = Modifier.fillMaxSize()) }
            composable("Analysis"){AnalyticsScreen(navController,modifier = Modifier.fillMaxSize())}
            composable("notesScreen"){NotesScreen(navController,modifier = Modifier.fillMaxSize()) }
            composable("EditNotesScreen") { EditNotesScreen(navController,modifier = Modifier.fillMaxSize()) }
            composable("AddBooks"){ AddBookScreen(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFFCCC2DC) // 设置导航栏颜色
    ) {
        val items = listOf(
            "Book" to Icons.Default.Book,
            "Bookshelf" to Icons.Default.LibraryBooks,
            "Analysis" to Icons.Default.BarChart,
        )
        items.forEach { (screen, icon) ->
            BottomNavigationItem(
                icon = { Icon(icon, contentDescription = screen) },
                label = { Text(screen) },
                selected = navController.currentDestination?.route == screen,
                onClick = {
                    navController.navigate(screen) {
                        // 清理导航栈，避免导航栈过深
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}


@Preview
@Composable
fun PreviewMainActivity() {
    AppNavigation()
}


