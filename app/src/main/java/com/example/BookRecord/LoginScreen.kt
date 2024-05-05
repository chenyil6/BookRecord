package com.example.BookRecord


import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun LoginScreen(
    //auth: FirebaseAuth,
    navController: NavController,
    signInLauncher: ActivityResultLauncher<Intent>, // 添加这个新参数
    googleSignInClient: GoogleSignInClient, // 这个应该从Activity或ViewModel传递到Composable
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = !isValidEmail(email)
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = emailError
        )
        if (emailError) {
            Text("Invalid email", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(5.dp))

        var password by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) } // 新增状态

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = !isValidPassword(password)
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // 根据状态选择是否显示密码
            trailingIcon = { // 添加眼睛图标作为尾随图标
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError
        )
        if (passwordError) {
            Text("Should be more than 8 characters", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = {
                //auth.signOut()
                if (!emailError && !passwordError) {
                    viewModel.loginUser(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Log In",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
// 观察登录状态，并根据状态进行导航或显示错误消息
        val loginStatus by viewModel.loginStatus.observeAsState()
        loginStatus?.let { isSuccess ->
            if (isSuccess) {
                LaunchedEffect(isSuccess) {
                    navController.navigate("Book")
                    {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                // 显示错误信息
                Text("Invalid email or password", color = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            TextButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    Log.d("GoogleSignIn", "Launching sign in intent.")
                    signInLauncher.launch(signInIntent) // 使用signInLauncher来启动登录意图
                }) {
                Text("Sign in with Google")
            }


            Spacer(Modifier.weight(1f))

            TextButton(onClick = {
                navController.navigate("Register")
            }) {
                Text("Register")
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 8 && password.any { it.isDigit() }
}

@Preview
@Composable
fun LoginScreen() {

}