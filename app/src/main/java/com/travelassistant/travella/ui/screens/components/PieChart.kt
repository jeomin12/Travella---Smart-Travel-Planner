//package com.travelassistant.travella.ui.screens.components
//
//// ui/screens/components/PieChart.kt
//
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
//import androidx.compose.ui.graphics.drawscope.rotate
//import androidx.compose.ui.unit.dp
//import com.travelassistant.travella.model.ExpenseItem
//import com.travelassistant.travella.utils.ExpenseCategory
//import java.util.*
//
//@Composable
//fun PieChart(expenses: List<ExpenseItem>) {
//    if (expenses.isEmpty()) {
//        Text("No data to display", modifier = Modifier.padding(16.dp))
//        return
//    }
//
//    val categorySums = expenses.groupBy { it.category }
//        .mapValues { entry -> entry.value.sumOf { it.amount } }
//
//    val total = categorySums.values.sum()
//    val angles = categorySums.mapValues { (_, value) -> 360f * (value / total.toFloat()) }
//
//    val colors = categorySums.map { (cat, _) ->
//        ExpenseCategory.fromLabel(cat).color
//    }
//
//    val labels = categorySums.map { (cat, amount) ->
//        "${cat}: $amount"
//    }
//
//    Column(modifier = Modifier
//        .fillMaxWidth()
//        .padding(8.dp)) {
//
//        Canvas(modifier = Modifier
//            .fillMaxWidth()
//            .height(220.dp)) {
//            var startAngle = -90f
//            val size = Size(size.width, size.height)
//            val radius = size.minDimension / 2f
//
//            angles.values.forEachIndexed { index, angle ->
//                drawArc(
//                    color = colors[index],
//                    startAngle = startAngle,
//                    sweepAngle = angle,
//                    useCenter = true,
//                    size = size
//                )
//                startAngle += angle
//            }
//
//            // Optional shadow for 3D-like effect
//            drawIntoCanvas { canvas ->
//                canvas.nativeCanvas.apply {
//                    drawCircle(
//                        size.width / 2,
//                        size.height / 2,
//                        radius,
//                        android.graphics.Paint().apply {
//                            color = android.graphics.Color.argb(25, 0, 0, 0)
//                        }
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Column(modifier = Modifier.fillMaxWidth()) {
//            labels.forEachIndexed { index, label ->
//                Row(modifier = Modifier.padding(vertical = 4.dp)) {
//                    Box(
//                        modifier = Modifier
//                            .size(12.dp)
//                            .background(colors[index])
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(label)
//                }
//            }
//        }
//    }
//}
