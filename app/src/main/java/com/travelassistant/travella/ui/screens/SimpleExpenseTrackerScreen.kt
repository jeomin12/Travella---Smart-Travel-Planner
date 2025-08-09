package com.travelassistant.travella.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.travelassistant.travella.utils.ExpenseCategory
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelassistant.travella.viewmodel.ExpenseViewModel
import com.travelassistant.travella.data.model.ExpenseEntity
import com.travelassistant.travella.ui.components.FilterChip
import android.app.DatePickerDialog
import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleExpenseTrackerScreen(
    navController: NavHostController,
    expenseViewModel: ExpenseViewModel = viewModel() // Inject ViewModel
) {
    val expenses by expenseViewModel.filteredExpenses.collectAsState(initial = emptyList())
    val totalExpenses by expenseViewModel.totalExpenses.collectAsState()
    val expenseCount = expenses.size
    val supportedCurrencies by expenseViewModel.supportedCurrencies.collectAsState()
    val displayCurrency by expenseViewModel.displayCurrency.collectAsState()
    val errorMessage by expenseViewModel.errorMessage.collectAsState()

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showCategoryFilterDropdown by remember { mutableStateOf(false) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    // State for date range filter
    val selectedDateRange by expenseViewModel.selectedDateRange.collectAsState()
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Expense Tracker",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Track every penny",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    // Currency Converter Dropdown
                    Box {
                        OutlinedButton(
                            onClick = { showCurrencyDropdown = !showCurrencyDropdown },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(displayCurrency)
                            Icon(
                                if (showCurrencyDropdown) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = showCurrencyDropdown,
                            onDismissRequest = { showCurrencyDropdown = false }
                        ) {
                            supportedCurrencies.forEach { currency ->
                                DropdownMenuItem(
                                    text = { Text(currency) },
                                    onClick = {
                                        expenseViewModel.updateDisplayCurrency(currency)
                                        showCurrencyDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddExpenseDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Expense") },
                text = { Text("Add Expense") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Error Message Display
            errorMessage?.let {
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    action = {
                        TextButton(onClick = { expenseViewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(it)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Summary Card
                item {
                    SummaryCard(totalExpenses, expenseCount, displayCurrency)
                }

                // Filter Section
                item {
                    Column {
                        Text(
                            "Filter Expenses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Category Filter
                            item {
                                Box {
                                    FilterChip(
                                        label = expenseViewModel.selectedCategory.collectAsState().value ?: "All Categories",
                                        selected = expenseViewModel.selectedCategory.collectAsState().value != null,
                                        onClick = { showCategoryFilterDropdown = !showCategoryFilterDropdown }
                                    )
                                    DropdownMenu(
                                        expanded = showCategoryFilterDropdown,
                                        onDismissRequest = { showCategoryFilterDropdown = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("All Categories") },
                                            onClick = {
                                                expenseViewModel.setCategoryFilter(null)
                                                showCategoryFilterDropdown = false
                                            }
                                        )
                                        ExpenseCategory.values().forEach { category ->
                                            DropdownMenuItem(
                                                text = { Text(category.label) },
                                                onClick = {
                                                    expenseViewModel.setCategoryFilter(category.label)
                                                    showCategoryFilterDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Date Range Filter
                            item {
                                FilterChip(
                                    label = selectedDateRange?.let {
                                        "${dateFormatter.format(Date(it.first))} - ${dateFormatter.format(Date(it.second))}"
                                    } ?: "All Dates",
                                    selected = selectedDateRange != null,
                                    onClick = { showDateRangePicker(context, expenseViewModel) }
                                )
                            }

                            // Clear Filters
                            item {
                                if (expenseViewModel.selectedCategory.collectAsState().value != null || selectedDateRange != null) {
                                    FilterChip(
                                        label = "Clear Filters",
                                        selected = false,
                                        onClick = { expenseViewModel.clearFilters() }
                                    )
                                }
                            }
                        }
                    }
                }

                // Expense List Header
                item {
                    Text(
                        "Recent Expenses",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Expense List
                if (expenses.isEmpty()) {
                    item {
                        EmptyExpenseCard()
                    }
                } else {
                    items(expenses, key = { it.id }) { expense ->
                        SimpleExpenseCard(
                            expense = expense,
                            displayCurrency = displayCurrency,
                            onDelete = { expenseViewModel.deleteExpense(it) }
                        )
                    }
                }

                // Spacer for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        SimpleAddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onSave = { newExpense ->
                expenseViewModel.addExpense(newExpense)
                showAddExpenseDialog = false
            }
        )
    }
}

private fun showDateRangePicker(context: Context, expenseViewModel: ExpenseViewModel) {
    val calendar = Calendar.getInstance()
    var startDate: Long? = null
    var endDate: Long? = null

    val endDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day, 23, 59, 59) // End of day
            endDate = calendar.timeInMillis
            if (startDate != null && endDate != null) {
                expenseViewModel.setDateRangeFilter(startDate, endDate)
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val startDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day, 0, 0, 0) // Start of day
            startDate = calendar.timeInMillis
            endDatePicker.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    startDatePicker.show()
}


@Composable
private fun SummaryCard(totalExpenses: Double, expenseCount: Int, displayCurrency: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    "Total Spent",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    "${displayCurrency} ${String.format("%.2f", totalExpenses)}",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$expenseCount transactions",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }

            Icon(
                Icons.Default.AttachMoney,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp),
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SimpleExpenseCard(
    expense: ExpenseEntity, // Changed to ExpenseEntity
    displayCurrency: String,
    onDelete: (ExpenseEntity) -> Unit // Changed to ExpenseEntity
) {
    val category = ExpenseCategory.values()
        .find { it.label == expense.category } ?: ExpenseCategory.Others
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Card(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = category.color.copy(alpha = 0.1f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        category.icon,
                        contentDescription = null,
                        tint = category.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                if (expense.description.isNotBlank()) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateFormatter.format(Date(expense.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (expense.location.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = expense.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Amount and Actions
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${displayCurrency} ${String.format("%.2f", expense.amountInUSD)}", // Display amountInUSD
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = { onDelete(expense) }, // Pass the expense object
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyExpenseCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "No expenses yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Start tracking your expenses by adding your first transaction",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
