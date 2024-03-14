package com.example.BookRecord
import android.graphics.Insets.add
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    // The focus manager to handle the keyboard actions
    val focusManager = LocalFocusManager.current

    // State for search text
    var searchText by remember { mutableStateOf("") }

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
                    tint = Color(0xFF6650a4)
                )
            }

            Spacer(modifier = Modifier.width(10.dp)) // Add space between the icon and the search bar

            // Search input field
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search by title, author", color = Color(0xFF6650a4)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(20.dp)), // 直接在这里设置背景颜色和形状
                shape = RoundedCornerShape(20.dp), // 设置输入框的形状
                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    backgroundColor = Color(0xFFF2F2F2),
                    cursorColor = Color(0xFF6650a4),
                    focusedBorderColor = Color(0xFF6650a4),
                    unfocusedBorderColor = Color(0xFF6650a4)
                ),
                singleLine = true,
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF6650a4)
                            )
                        }
                    }
                },
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus() // Hide the keyboard
                        // TODO: Implement the search logic here
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
            )
        }

        // TODO: Add the rest of your UI components here
    }
}

