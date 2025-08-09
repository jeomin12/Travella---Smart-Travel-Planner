//package com.travelassistant.travella.ui.screens
//
//// ui/screens/ReportScreen.kt
//
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.text.selection.SelectionContainer
//import androidx.compose.material.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.travelassistant.travella.model.ExpenseItem
//import com.travelassistant.travella.utils.ExpenseCategory
//
//@Composable
//fun ReportScreen(
//    expenses: List<ExpenseItem>,
//    onBack: () -> Unit
//) {
//    val categorySums = expenses.groupBy { it.category }
//        .mapValues { it.value.sumOf { item -> item.amount } }
//
//    val total = expenses.sumOf { it.amount }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Expense Report") }, navigationIcon = {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                }
//            })
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//            Text("Total Spent: \$${String.format("%.2f", total)}", style = MaterialTheme.typography.h6)
//            Spacer(modifier = Modifier.height(12.dp))
//
//            LazyColumn {
//                categorySums.forEach { (category, amount) ->
//                    val color = ExpenseCategory.fromLabel(category).color
//                    item {
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 6.dp),
//                            elevation = 4.dp
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .padding(12.dp)
//                                    .fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Text(category, color = color)
//                                Text("\$${String.format("%.2f", amount)}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
//                            }
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            Text("Export Options", style = MaterialTheme.typography.subtitle1)
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//                Button(onClick = { /* TODO: Export to PDF */ }) { Text("PDF") }
//                Button(onClick = { /* TODO: Export to Excel */ }) { Text("XLS") }
//                Button(onClick = { /* TODO: Share via Email */ }) { Text("Email") }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            SelectionContainer {
//                Text("Note: Export functionality is coming soon. This is a visual placeholder.")
//            }
//        }
//    }
//}
