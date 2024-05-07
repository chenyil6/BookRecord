package com.example.BookRecord

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Observer
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.jakewharton.threetenabp.AndroidThreeTen


class MainActivity : ComponentActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var signInResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navController: NavHostController  // 删除 'private var' 的声明，改为 lateinit
    // 添加一个属性来保存 RegisterViewModel 的引用
//    private val registerViewModel by viewModels<RegisterViewModel> {
//        RegisterViewModelFactory(UserRepository(getSharedPreferences("app_prefs", MODE_PRIVATE)))
//    }
    private val registerViewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory(AppDatabase.getDatabase(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setupGoogleSignIn()  // 调用新的方法来初始化谷歌登录

        setContent {
            val notesViewModel: NoteViewModel by viewModels()
            val bookViewModel: BookViewModel by viewModels()
            val readingRecordViewModel:ReadingRecordViewModel by viewModels()
// 初始化 navController
            navController = rememberNavController()
            BookRecordTheme {
                CompositionLocalProvider(
                    LocalNotesViewModel provides notesViewModel,
                    LocalBooksViewModel provides bookViewModel,
                    LocalreadingRecordViewModel provides readingRecordViewModel,
                ) {
                    // 不再在这里声明 navController，直接传递上面初始化的 navController
                    AppNavigation(navController, registerViewModel, googleSignInClient, signInResultLauncher)
                }
            }
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signInResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("GoogleSignIn", "Activity Result received")
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                Log.d("GoogleSignIn", "Sign in failed or cancelled")
            }
        }

    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken ?: throw Exception("Google ID Token is null")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val firebaseUser = authTask.result?.user ?: throw Exception("Firebase user is null")
                    val userId = firebaseUser.uid
                    registerViewModel.checkAndInsertUser(userId).observe(this, Observer { userInserted ->
                        if (userInserted || registerViewModel.userAlreadyExists) {
                            navController.navigate("Book")  // 如果用户新插入或已存在，都导航至Book页面
                        } else {
                            // 处理用户插入失败情况
                            Log.d("SignIn", "User insertion failed due to an error.")
                            Toast.makeText(this, "An error occurred during user insertion", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Log.e("SignIn", "Firebase Sign-In failed: ${authTask.exception?.localizedMessage}")
                    Toast.makeText(this, "Firebase Google Sign-In failed: ${authTask.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: ApiException) {
            Log.e("SignIn", "Google Sign-In failed code=${e.statusCode}")
            Toast.makeText(this, "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    class RegisterViewModelFactory(private val appDatabase: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                return RegisterViewModel(UserRepository(appDatabase)) as T
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
    val currentUser = observeUserAuthenticationState().value
    val startDestination = if (currentUser != null) "Book" else "LoginScreen"

    //val navController = rememberNavController()
    // 根据当前的导航目的地决定是否显示底部导航栏
    val shouldShowBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route !in listOf("notesScreen","EditNotesScreen","LoginScreen","AddBooks","Register")
    Scaffold(
        bottomBar = { if (shouldShowBottomBar) {
            BottomNavigationBar(navController) }
        }
    ) { innerPadding ->
        // 利用提供的 innerPadding 参数调整内容的内边距
        NavHost(
            navController = navController,
            startDestination = startDestination,
            //startDestination = "LoginScreen",
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
            composable("Book") { HomeScreen(googleSignInClient,navController,modifier = Modifier.fillMaxSize())}
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

@Composable
fun observeUserAuthenticationState(): State<FirebaseUser?> {
    val auth = FirebaseAuth.getInstance()
    val currentUser = remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser.value = auth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    return currentUser
}
