//package com.travelassistant.travella.ui.screens.components
//
//// ui/screens/components/BarChart.kt
//
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.travelassistant.travella.model.ExpenseItem
//import com.travelassistant.travella.utils.ExpenseCategory
//
//@Composable
//fun BarChart(expenses: List<ExpenseItem>) {
//    if (expenses.isEmpty()) {
//        Text("No data to display", modifier = Modifier.padding(16.dp))
//        return
//    }
//
//    val categorySums = expenses.groupBy { it.category }
//        .mapValues { it.value.sumOf { item -> item.amount } }
//
//    val maxAmount = categorySums.maxOf { it.value }
//
//    val barWidth = 32.dp
//    val spacing = 16.dp
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Category-wise Expense", modifier = Modifier.padding(bottom = 8.dp))
//
//        Row(
//            verticalAlignment = Alignment.Bottom,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(180.dp)
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            categorySums.entries.forEach { (category, amount) ->
//                val barHeightRatio = (amount / maxAmount).toFloat()
//                val color = ExpenseCategory.fromLabel(category).color
//
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "${amount.toInt()}",
//                        modifier = Modifier.padding(bottom = 4.dp)
//                    )
//
//                    Canvas(
//                        modifier = Modifier
//                            .height((barHeightRatio * 140).dp)
//                            .width(barWidth)
//                    ) {
//                        drawRoundRect(
//                            color = color,
//                            size = size,
//                            cornerRadius = CornerRadius(12f, 12f)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Text(
//                        text = category,
//                        modifier = Modifier.width(barWidth + 12.dp),
//                        maxLines = 1
//                    )
//                }
//            }
//        }
//    }
//}
