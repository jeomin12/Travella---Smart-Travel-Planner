//package com.travelassistant.travella.ui.screens.components
//
//// ui/screens/components/CalendarSelector.kt
//
//
//import android.app.DatePickerDialog
//import android.widget.DatePicker
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Button
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Composable
//fun CalendarSelector(onDateRangeSelected: (Long, Long) -> Unit) {
//    val context = LocalContext.current
//    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//    val calendar = Calendar.getInstance()
//
//    var startDate by remember { mutableStateOf(calendar.timeInMillis) }
//    var endDate by remember { mutableStateOf(calendar.timeInMillis) }
//
//    Column(Modifier.fillMaxWidth()) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Button(onClick = {
//                DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, day: Int ->
//                    calendar.set(year, month, day)
//                    startDate = calendar.timeInMillis
//                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
//            }) {
//                Text("Start: ${dateFormat.format(Date(startDate))}")
//            }
//
//            Button(onClick = {
//                DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, day: Int ->
//                    calendar.set(year, month, day)
//                    endDate = calendar.timeInMillis
//                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
//            }) {
//                Text("End: ${dateFormat.format(Date(endDate))}")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Button(
//            onClick = { onDateRangeSelected(startDate, endDate) },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Filter")
//        }
//    }
//}
