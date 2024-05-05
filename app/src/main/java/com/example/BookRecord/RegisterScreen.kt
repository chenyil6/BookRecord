package com.example.BookRecord

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
        var passwordVisible by remember { mutableStateOf(false) }
        var username by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("") }
        var genderError by remember { mutableStateOf(false) }

        // 用户名输入框
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 电话号码输入框
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 性别选择框
        OutlinedTextField(
            value = gender,
            onValueChange = {
                gender = it
                genderError = !listOf("Male", "Female", "Other").contains(it)
            },
            label = { Text("Gender") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = genderError
        )
        if (genderError) {
            Text("Please enter Male, Female, or Other", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        // 密码输入框
        var minLength by remember { mutableStateOf(false) }
        var hasNumber by remember { mutableStateOf(false) }
        val context = LocalContext.current  // 获取当前 Compose 的 Context

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                minLength = password.length >= 8
                hasNumber = password.any { it.isDigit() }
                passwordError = !(minLength && hasNumber) // Ensure both conditions are met
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Visibility is based on the toggle state
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError
        )

        //Spacer(modifier = Modifier.height(10.dp))
// 密码规则说明
        Column(modifier = Modifier.padding(top = 8.dp)) {
            PasswordRule("Minimum 8 characters", minLength)
            PasswordRule("At least one number", hasNumber)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 注册按钮
        Button(
            onClick = {
                if (!emailError && !passwordError && !genderError) {
                    val userInfo = UserInfo(
                        email = email,
                        username = username,
                        phoneNumber = phoneNumber,
                        gender = gender
                    )
                    viewModel.registerUser(email, password, userInfo)
                    Toast.makeText(context, "Registration successful. Please login.", Toast.LENGTH_LONG).show()
                    navController.navigate("LoginScreen"){
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !emailError && !passwordError && !genderError
        ) {
            Text(
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 已经有账号？返回登录界面
        TextButton(onClick = { navController.navigate("LoginScreen") }) {
            Text("Already have an account? Log In")
        }
    }
}


@Composable
fun PasswordRule(text: String, isValid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {
        Icon(
            imageVector = if (isValid) Icons.Filled.CheckCircle else Icons.Filled.Error,
            contentDescription = null,
            tint = if (isValid) Color(0xC36FB147) else Color(0xFFF44336),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = if (isValid) Color(0xC36FB147) else Color(0xFFF44336), style = MaterialTheme.typography.bodyMedium)
    }
}




@Preview
@Composable
fun RegisterScreen() {

}
