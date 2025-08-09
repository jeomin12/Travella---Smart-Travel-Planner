//package com.travelassistant.travella.ui.screens.components
//
//// ui/screens/components/ExpenseCard.kt
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Card
//import androidx.compose.material.Icon
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.travelassistant.travella.model.ExpenseItem
//import com.travelassistant.travella.utils.ExpenseCategory
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Composable
//fun ExpenseCard(expense: ExpenseItem) {
//    val category = ExpenseCategory.fromLabel(expense.category)
//    val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.date))
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp)
//            .shadow(2.dp, shape = RoundedCornerShape(12.dp)),
//        backgroundColor = Color.White,
//        elevation = 4.dp,
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(color = category.color.copy(alpha = 0.2f), shape = RoundedCornerShape(10.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = category.icon,
//                    contentDescription = category.label,
//                    tint = category.color
//                )
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = expense.itemName,
//                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
//                )
//                Text(
//                    text = "${category.label} | $dateFormatted | ${expense.method}",
//                    style = MaterialTheme.typography.body2.copy(color = Color.Gray)
//                )
//                if (expense.notes.isNotEmpty()) {
//                    Text(
//                        text = expense.notes,
//                        style = MaterialTheme.typography.body2.copy(color = Color.DarkGray)
//                    )
//                }
//            }
//
//            Text(
//                text = "- \$${expense.amount}",
//                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
//                color = Color.Red
//            )
//        }
//    }
//}
