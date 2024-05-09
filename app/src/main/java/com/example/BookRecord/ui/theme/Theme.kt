package com.example.BookRecord.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.content.Context

object ThemeManager {
    private const val THEME_PREF = "theme_preferences"
    private const val THEME_KEY = "theme_color"

    fun getColorScheme(context: Context): ColorScheme {
        val prefs = context.getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)
        val colorName = prefs.getString(THEME_KEY, "purple")  // 默认为紫色
        return when (colorName) {
            "blue" -> BlueColorScheme
            "green" -> GreenColorScheme
            else -> PurpleColorScheme
        }
    }

    fun setColorScheme(context: Context, colorName: String) {
        val prefs = context.getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(THEME_KEY, colorName).apply()
    }
}

// 色彩方案定义
val PurpleColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = Purple80,
    tertiary = PurpleGrey80,
    background = LightPurple,
    secondaryContainer = LightGreyPurple
)

val BlueColorScheme = lightColorScheme(
    primary = Blue,
    secondary = BlueLight,
    tertiary = BlueGrey,
    background = LightBlue,
    secondaryContainer = LightGreyBlue
)

val GreenColorScheme = lightColorScheme(
    primary = Green,
    secondary = GreenLight,
    tertiary = GreenGrey,
    background = LightGreen,
    secondaryContainer = LightGreyGreen
)

@Composable
fun BookRecordTheme(
    themeColorState: State<ColorScheme>,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = remember { mutableStateOf(ThemeManager.getColorScheme(context)) }

    MaterialTheme(
        colorScheme = themeColorState.value,
        typography = Typography,
        content = content
    )
}

