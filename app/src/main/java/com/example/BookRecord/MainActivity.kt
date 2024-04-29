package com.example.BookRecord

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.BookRecord.ui.theme.AnalyticsScreen
import com.example.BookRecord.ui.theme.BookRecordTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.jakewharton.threetenabp.AndroidThreeTen



class MainActivity : ComponentActivity() {

    private lateinit var signInResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navController: NavHostController  // 删除 'private var' 的声明，改为 lateinit
    // 添加一个属性来保存 RegisterViewModel 的引用
    private val registerViewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory(UserRepository(getSharedPreferences("app_prefs", MODE_PRIVATE)))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        // 初始化 Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 注册 Google 登录的 ActivityResultLauncher
        signInResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

        setContent {
            val notesViewModel: NoteViewModel by viewModels()
            val bookViewModel: BookViewModel by viewModels()
// 初始化 navController
            navController = rememberNavController()
            BookRecordTheme {
                CompositionLocalProvider(
                    LocalNotesViewModel provides notesViewModel,
                    LocalBooksViewModel provides bookViewModel
                ) {
                    // 不再在这里声明 navController，直接传递上面初始化的 navController
                    AppNavigation(navController, registerViewModel, googleSignInClient, signInResultLauncher)
                }
            }
        }
    }
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            // 登录成功，使用 account 信息，导航到 Book 页面
            navController.navigate("Book")
        } catch (e: ApiException) {
            // 登录失败，处理异常
            Log.e("SignIn", "signInResult:failed code=" + e.statusCode)
        }
    }
    class RegisterViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    navController: NavHostController,  // 更改类型为 NavHostController
    //activity: MainActivity,
    registerViewModel: RegisterViewModel,
    googleSignInClient: GoogleSignInClient,
    signInResultLauncher: ActivityResultLauncher<Intent>
) {
    //val navController = rememberNavController()
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
                //LoginScreen(navController, modifier = Modifier.fillMaxSize())
                LoginScreen(navController,signInResultLauncher,googleSignInClient, modifier = Modifier.fillMaxSize(), registerViewModel)

            }
            composable("Register") {
                // 获取一个与当前 Composable 生命周期相关联的 ViewModel 实例
                //val registerViewModel: RegisterViewModel = viewModel()

                // 调用 RegisterScreen 并将 viewModel 传递进去
                RegisterScreen(navController, registerViewModel, modifier = Modifier.fillMaxSize())
            }
            composable("Book") { HomeScreen(navController,modifier = Modifier.fillMaxSize())}
            composable("Bookshelf"){BookShelf(navController,modifier = Modifier.fillMaxSize()) }
            composable("Analysis"){AnalyticsScreen(navController,modifier = Modifier.fillMaxSize())}
            composable("notesScreen/{bookId}") { backStackEntry ->
                // Extract the bookId parameter from the backStackEntry
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                if (bookId == null) {
                    // 无法解析bookId，根据你的应用逻辑处理这种情况
                    // 比如返回上一屏或显示一个错误消息
                } else {
                    NotesScreen(navController, bookId, modifier = Modifier.fillMaxSize())
                }
            }
            composable("EditNotesScreen/{bookId}") { backStackEntry ->
                // Extract the bookId parameter from the backStackEntry
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                if (bookId == null) {
                    // 无法解析bookId，根据你的应用逻辑处理这种情况
                    // 比如返回上一屏或显示一个错误消息
                } else {
                    EditNotesScreen(navController, bookId, modifier = Modifier.fillMaxSize())
                }
            }
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

}


