package com.example.BookRecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel, // 保留 viewModel 参数
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf(false) }

        // Email 输入框
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

        // 密码输入框
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = !isValidPassword(password)
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError
        )
        if (passwordError) {
            Text("Password does not meet requirements", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 注册按钮
        Button(
            onClick = {
                if (!emailError && !passwordError) {
                    // 调用 ViewModel 中的注册方法
                    viewModel.registerUser(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !emailError && !passwordError
        ) {
            Text(
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 观察注册状态
        val registrationStatus by viewModel.registrationStatus.observeAsState(false)
        if (registrationStatus) {
            // 注册成功后导航到登录界面
            LaunchedEffect(key1 = registrationStatus) {
                navController.navigate("LoginScreen")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 已经有账号？返回登录界面
        TextButton(onClick = { navController.navigate("LoginScreen") }) {
            Text("Already have an account? Log In")
        }
    }
}


@Preview
@Composable
fun RegisterScreen() {

}
