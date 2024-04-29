package com.example.BookRecord.ui.theme

import android.icu.text.SimpleDateFormat
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.Instant
import java.util.Date
import java.util.Locale

// Define your color constants
val colorPrimary = Color(0xFF8C73B8)
val colorOnPrimary = Color(0xFFCCCCCC) // for text on primary color background
val colorRead = Color(0xBEB09AD8) // Green
val colorUnread = Color(0xD29CC9E7) // Red
val colorReading = Color(0xFF9EE0C2) // Blue
val colorBackground = Color(0xFFFFFFFF) // White or any other background color

// Define some dimension constants
val spaceSmall = 8.dp
val spaceMedium = 16.dp
val spaceLarge = 24.dp
val chartHeight = 200.dp
val pieChartSize = 200.dp


// Sample data for the bar chart, showing pages read each day.
val readingData7Days = listOf(5, 2, 7, 3, 5, 4, 13) // 近7天
val readingData15Days = List(15) { (1..20).random() } // 随机生成近30天的数据

// Sample data for the pie chart, showing book status distribution.
val bookShelfData = mapOf("have read" to 33, "lay aside" to 12, "reading" to 6)


@Composable
fun BarChart(data: List<Int>, modifier: Modifier = Modifier
    .fillMaxWidth()
    .height(200.dp)) {
    Canvas(modifier = modifier) {
        val maxCount = data.maxOrNull() ?: 1 // 保证除数不为零
        val barWidth = size.width / (data.size * 2f)

        data.forEachIndexed { index, count ->
            val barHeight = size.height * (count / maxCount.toFloat())
            val barTopLeft = Offset((barWidth + barWidth * 2 * index), size.height - barHeight)
            drawRect(
                color = colorPrimary, // 假设使用蓝色表示
                topLeft = barTopLeft,
                size = Size(barWidth, barHeight)
            )

            // 新增部分，用于绘制文本
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK // 文本颜色
                textSize = 35f // 文本大小
                textAlign = android.graphics.Paint.Align.CENTER // 文本对齐方式
            }
            drawContext.canvas.nativeCanvas.drawText(
                count.toString(), // 要绘制的文本
                barTopLeft.x + barWidth / 2, // 文本的x坐标，使其居中于柱子之上
                barTopLeft.y - 5f, // 文本的y坐标，略高于柱子顶部
                textPaint // 使用的画笔
            )
        }
    }
}


@RequiresApi(0)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayDatePicker() {
    val calendar = android.icu.util.Calendar.getInstance()
    calendar.set(2024, 0, 1) // month (0) is January
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var selectedDate by remember {
        mutableStateOf(calendar.timeInMillis)
    }
    Column(modifier = Modifier.padding(0.dp)) {
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        showDatePicker = false
                        selectedDate = datePickerState.selectedDateMillis!!
                        //selectedDateMillis!! null safety because type declared as Long? selectedDate = datePickerState.selectedDateMillis!!
                    }) {
                        androidx.compose.material3.Text(text = "OK")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        showDatePicker = false
                    }) {
                        androidx.compose.material3.Text(text = "Cancel")
                    }
                }
            ) //end of dialog
            { //still column scope
                DatePicker(
                    state = datePickerState
                )
            }
        }// end of if
        androidx.compose.material3.Button(
            onClick = {
                showDatePicker = true
            },
            shape = RectangleShape,// 这将按钮设为方形
            modifier = Modifier.size(160.dp,35.dp)
        ) {
            androidx.compose.material3.Text(text = "End Date Selector")
        }
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        Spacer(modifier = Modifier.height(10.dp))
        androidx.compose.material3.Text(

            text = "   Data Until: ${formatter.format(Date(selectedDate))}"
        )
    }
}


//@Composable
//fun PieChart(data: Map<String, Int>, modifier: Modifier = Modifier.size(200.dp)) {
//    Canvas(modifier = modifier) {
//        val total = data.values.sum()
//        var startAngle = -90f // 扇形图的起始角度
//
//        data.forEach { (category, count) ->
//            val sweepAngle = (count / total.toFloat()) * 360f // 扇形图的角度
//            val color = when (category) { // 根据分类获取颜色
//                "已读" -> colorRead
//                "未读" -> colorUnread
//                "在读" -> colorReading
//                else -> Color.LightGray
//            }
//            drawArc(
//                color = color, // 使用上面定义的颜色
//                startAngle = startAngle,
//                sweepAngle = sweepAngle,
//                useCenter = true,
//                size = Size(size.width, size.height) // 使用整个Canvas的尺寸
//            )
//            startAngle += sweepAngle // 下一个扇形的起始角度
//        }
//    }
//}

@Composable
fun PieChart(data: Map<String, Int>, modifier: Modifier = Modifier.size(150.dp)) {
    Canvas(modifier = modifier) {
        val total = data.values.sum()
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2
        val holeRadius = radius * 0.6f // 中心空洞的半径
        var startAngle = -90f // 扇形图的起始角度

        data.forEach { (category, count) ->
            val sweepAngle = (count / total.toFloat()) * 360f // 扇形图的角度
            val color = when (category) { // 根据分类获取颜色
                "have read" -> colorRead
                "lay aside" -> colorUnread
                "reading" -> colorReading
                else -> Color.LightGray
            }
            drawArc(
                color = color, // 使用上面定义的颜色
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false, // 画环形
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2), // 环形的尺寸
                style = Stroke(width = radius - holeRadius) // 环形的宽度
            )

            // 如果需要在每个扇区中绘制数字，可以在这里添加代码

            startAngle += sweepAngle // 下一个扇形的起始角度
        }

        // 绘制中心的文本
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK // 文本颜色
            textSize = 40f // 文本大小
            textAlign = android.graphics.Paint.Align.CENTER // 文本对齐方式
        }
        drawContext.canvas.nativeCanvas.drawText(
            "$total Books", // 中心的文字
            center.x, // 中心点x坐标
            center.y - (textPaint.ascent() + textPaint.descent()) / 2, // 中心点y坐标，垂直居中
            textPaint // 使用的画笔
        )
    }
}

@Composable
fun Legend(data: Map<String, Int>, modifier: Modifier) {
    Column(modifier = modifier.padding(10.dp)) {
        data.forEach { (category, count) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 这里绘制标签颜色的小方块
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            when (category) {
                                "have read" -> colorRead
                                "lay aside" -> colorUnread
                                "reading" -> colorReading
                                else -> Color.LightGray
                            }
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
                // 这里是类别名称和数量
                Text(text = "$category $count",modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun BookStatusPieChart(data: Map<String, Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(start = 35.dp), // 这里添加左边距
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 饼状图占据行的一半宽度
        PieChart(data = data)
        Spacer(modifier = Modifier.width(60.dp))
        // 标签列表占据剩下的宽度
        Legend(data = data, modifier = Modifier.weight(1f))
    }
}



@Composable
fun TimeRangeSelection(timeRange: String, onTimeRangeSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.Bottom) {
        Button(
            onClick = { onTimeRangeSelected("Last 7 Days") },
            modifier = Modifier
                .padding(end = 0.dp)
                .size(129.dp, 35.dp),// 在按钮的右边添加间距
            colors = ButtonDefaults.buttonColors(backgroundColor = if (timeRange == "Las 7 Days") colorOnPrimary else Color.White)
        ) {
            Text("Last 7 Days")
        }
        Spacer(modifier = Modifier.height(7.dp))
        Button(
            modifier = Modifier.size(129.dp,35.dp),
            onClick = { onTimeRangeSelected("Last 15 Days") },
            colors = ButtonDefaults.buttonColors(backgroundColor = if (timeRange == "Las 7 Days") colorOnPrimary else Color.White)
        ) {
            Text("Last 15 Days")
        }
    }
}


@Composable
fun AnalyticsPage() {
    // State for the time range selection for the bar chart.
    var timeRange by remember { mutableStateOf("Last 7 Days") }

    // Function to calculate total pages read in the selected time range.
    fun calculateTotalPages(): Int {
        return if (timeRange == "Last 7 Days") readingData7Days.sum() else readingData15Days.sum()
    }

    // State for the total pages read.
    val totalPages by remember { mutableStateOf(calculateTotalPages()) }
    val scrollState = rememberScrollState()//创建可滚动状态

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(15.dp)
        .verticalScroll(scrollState)
        ) { // 加上padding使内容不要紧贴屏幕边缘
        androidx.compose.material3.Text(
            text = "Reading Statistics",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6650a4),
            modifier = Modifier.padding(end = 5.dp) // 根据需要调整文本的右边距
        )
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)){
//            Column(modifier = Modifier.fillMaxWidth()) {
//                Spacer(modifier = Modifier.height(5.dp))
//                Text(text = "Reading Statistics",
//                    fontSize = 25.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF6650a4),
//                    modifier = Modifier.padding(end = 5.dp)
//                    )
//                Spacer(modifier = Modifier.height(5.dp))
//                Divider(color = Color.LightGray, thickness = 0.5.dp)
//            }
//        }
        Spacer(modifier = Modifier.height(20.dp)) //
        Text(
            text = "✔️ Page Count Statistics",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF01051D),
            modifier = Modifier.align(Alignment.Start) //
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            DisplayDatePicker()
            Spacer(modifier = Modifier.weight(1f)) // 这个Spacer会占据所有可用空间
            TimeRangeSelection(timeRange) { selectedRange ->timeRange = selectedRange}
        }
        Spacer(modifier = Modifier.height(15.dp)) // 添加间隔
        BarChart(if (timeRange == "Last 7 Days") readingData7Days else readingData15Days)
        Spacer(modifier = Modifier.height(15.dp)) // 添加间隔
        Text(
            text = " Total Pages Read in the $timeRange： $totalPages",
            style = MaterialTheme.typography.h6,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(50.dp)) // 标题和扇形图之间的间隔
        Text(
            "📖 Book Status",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF01051D),
            modifier = Modifier.align(Alignment.Start) //
        )
        Spacer(modifier = Modifier.height(60.dp)) // 标题和扇形图之间的间隔
        BookStatusPieChart(data = bookShelfData)
    }
}


@Composable
fun AnalyticsScreen(navController: NavController, modifier: Modifier = Modifier) {
    // 可以在这里添加你之前创建的 BarChart 和 PieChart 组件
    AnalyticsPage()
}



