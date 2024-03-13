package com.example.BookRecord.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Define your color constants
val colorPrimary = Color(0xFF62548A)
val colorOnPrimary = Color(0xFFCCCCCC) // for text on primary color background
val colorRead = Color(0xFFE67FA2) // Green
val colorUnread = Color(0xFF4C568F) // Red
val colorReading = Color(0xFF93B0C7) // Blue
val colorBackground = Color(0xFFFFFFFF) // White or any other background color

// Define some dimension constants
val spaceSmall = 8.dp
val spaceMedium = 16.dp
val spaceLarge = 24.dp
val chartHeight = 200.dp
val pieChartSize = 200.dp


// Sample data for the bar chart, showing pages read each day.
val readingData7Days = listOf(5, 2, 7, 3, 5, 4, 13) // 近7天
val readingData30Days = List(30) { (1..6).random() } // 随机生成近30天的数据

// Sample data for the pie chart, showing book status distribution.
val bookShelfData = mapOf("已读" to 33, "未读" to 12, "在读" to 6)


@Composable
fun BarChart(data: List<Int>, modifier: Modifier = Modifier.fillMaxWidth().height(200.dp)) {
//    val primaryColor = MaterialTheme.colors.primary // 获取主题颜色

    Canvas(modifier = modifier) {
        val maxCount = data.maxOrNull() ?: 0
        val barWidth = size.width / (data.size * 2f)

        data.forEachIndexed { index, count ->
            val barHeight = size.height * (count / maxCount.toFloat())
            val barTopLeft = Offset((barWidth + barWidth * 2 * index), size.height - barHeight)
            drawRect(
                color = colorPrimary, // 使用传递进来的颜色
                topLeft = barTopLeft,
                size = Size(barWidth, barHeight)
            )
        }
    }
}



@Composable
fun PieChart(data: Map<String, Int>, modifier: Modifier = Modifier.size(200.dp)) {
    Canvas(modifier = modifier) {
        val total = data.values.sum()
        var startAngle = -90f // 扇形图的起始角度

        data.forEach { (category, count) ->
            val sweepAngle = (count / total.toFloat()) * 360f // 扇形图的角度
            val color = when (category) { // 根据分类获取颜色
                "已读" -> colorRead
                "未读" -> colorUnread
                "在读" -> colorReading
                else -> Color.LightGray
            }
            drawArc(
                color = color, // 使用上面定义的颜色
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height) // 使用整个Canvas的尺寸
            )
            startAngle += sweepAngle // 下一个扇形的起始角度
        }
    }
}

@Composable
fun TimeRangeSelection(timeRange: String, onTimeRangeSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { onTimeRangeSelected("近7天") },
            modifier = Modifier.padding(end = 0.dp), // 在按钮的右边添加间距
            colors = ButtonDefaults.buttonColors(backgroundColor = if (timeRange == "近7天") colorOnPrimary else Color.White)
        ) {
            Text("近7天")
        }
        Spacer(modifier = Modifier.width(spaceMedium))
        Button(
            onClick = { onTimeRangeSelected("近30天") },
            colors = ButtonDefaults.buttonColors(backgroundColor = if (timeRange == "近30天") colorOnPrimary else Color.White)
        ) {
            Text("近30天")
        }
    }
}


@Composable
fun AnalyticsPage() {
    // State for the time range selection for the bar chart.
    var timeRange by remember { mutableStateOf("近7天") }

    // Function to calculate total pages read in the selected time range.
    fun calculateTotalPages(): Int {
        return if (timeRange == "近7天") readingData7Days.sum() else readingData30Days.sum()
    }

    // State for the total pages read.
    val totalPages by remember { mutableStateOf(calculateTotalPages()) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(15.dp)) { // 加上padding使内容不要紧贴屏幕边缘
        Text(
            text = "阅读统计",
            style = TextStyle(
                fontSize = 25.sp, // 字体大小
                fontWeight = FontWeight.Medium, // 字重
                letterSpacing = 2.sp // 字符间距
            ),
            color = Color(0xFF01051D),
            modifier = Modifier.align(Alignment.CenterHorizontally) // 标题居中对齐
        )
        Spacer(modifier = Modifier.height(20.dp)) //
        Text(
            text = "✔️ 阅读页数统计",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF01051D),
            modifier = Modifier.align(Alignment.Start) //
        )
        Spacer(modifier = Modifier.height(15.dp)) // 标题和扇形图之间的间隔
        TimeRangeSelection(timeRange) { selectedRange ->
            timeRange = selectedRange
        }
        BarChart(if (timeRange == "近7天") readingData7Days else readingData30Days)
        Spacer(modifier = Modifier.height(10.dp)) // 添加间隔
        Text(
            text = "$timeRange 总阅读页数: $totalPages",
            style = MaterialTheme.typography.h6,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp)) // 标题和扇形图之间的间隔
        Text(
            "📖 书架分布统计",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF01051D),
            modifier = Modifier.align(Alignment.Start) //
        )
        Spacer(modifier = Modifier.height(15.dp)) // 标题和扇形图之间的间隔
        PieChart(
            data = bookShelfData,
            modifier = Modifier
                .size(230.dp) // 设置扇形图的尺寸
                .align(Alignment.CenterHorizontally) // 扇形图居中对齐
        )
    }
}


@Composable
fun AnalyticsScreen(navController: NavController, modifier: Modifier = Modifier) {
    // 可以在这里添加你之前创建的 BarChart 和 PieChart 组件
    AnalyticsPage()
}



