package com.travelassistant.travella.ui.screens


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.travelassistant.travella.utils.ExpenseCategory
import java.text.SimpleDateFormat
import java.util.*
import com.travelassistant.travella.data.model.SimpleExpense


@Composable
fun SimpleAddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (SimpleExpense) -> Unit
) {
    val context = LocalContext.current

    // Form state
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val selectedCurrency = "USD" // Fixed to USD as per requirement
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.Others) }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // UI state
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showPaymentDropdown by remember { mutableStateOf(false) }

    // Data
    val paymentMethods = listOf("Cash", "Credit Card", "Debit Card", "Mobile Payment", "Bank Transfer")

    // Date/Time formatters
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        showError = false
                    },
                    label = { Text("Title *") },
                    leadingIcon = {
                        Icon(Icons.Default.Title, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && title.isBlank(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Amount Field
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        // Allow only numbers and a single decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                            showError = false
                        }
                    },
                    label = { Text("Amount *") },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = showError && amount.isBlank()
                )

                // Category Selection (Simple Dropdown)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        leadingIcon = {
                            Icon(selectedCategory.icon, contentDescription = null)
                        },
                        trailingIcon = {
                            Icon(
                                if (showCategoryDropdown) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDropdown = !showCategoryDropdown }
                    )

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        ExpenseCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            category.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = category.color
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(category.label)
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                    if (title.isBlank()) {
                                        title = category.label
                                    }
                                }
                            )
                        }
                    }
                }

                // Payment Method (Simple Dropdown)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Payment Method") },
                        leadingIcon = {
                            Icon(
                                when (paymentMethod) {
                                    "Cash" -> Icons.Default.Money
                                    "Credit Card", "Debit Card" -> Icons.Default.CreditCard
                                    "Mobile Payment" -> Icons.Default.PhoneAndroid
                                    "Bank Transfer" -> Icons.Default.AccountBalance
                                    else -> Icons.Default.Payment
                                },
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                if (showPaymentDropdown) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPaymentDropdown = !showPaymentDropdown }
                    )

                    DropdownMenu(
                        expanded = showPaymentDropdown,
                        onDismissRequest = { showPaymentDropdown = false }
                    ) {
                        paymentMethods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    paymentMethod = method
                                    showPaymentDropdown = false
                                }
                            )
                        }
                    }
                }

                // Date Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = selectedDate
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(year, month, day)
                                    selectedDate = newCal.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            dateFormatter.format(Date(selectedDate)),
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = selectedTime
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    val newCal = Calendar.getInstance()
                                    newCal.timeInMillis = selectedTime
                                    newCal.set(Calendar.HOUR_OF_DAY, hour)
                                    newCal.set(Calendar.MINUTE, minute)
                                    selectedTime = newCal.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            timeFormatter.format(Date(selectedTime)),
                            fontSize = 12.sp
                        )
                    }
                }

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                // Location Field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Error message
                if (showError && errorMessage.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validation
                    when {
                        title.isBlank() -> {
                            showError = true
                            errorMessage = "Title is required"
                            return@Button
                        }
                        amount.isBlank() -> {
                            showError = true
                            errorMessage = "Amount is required"
                            return@Button
                        }
                        amount.toDoubleOrNull() == null || amount.toDouble() <= 0 -> {
                            showError = true
                            errorMessage = "Please enter a valid amount"
                            return@Button
                        }
                        else -> {
                            // Create the expense
                            val amountValue = amount.toDouble()

                            // Combine date and time
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = selectedDate
                            val timeCalendar = Calendar.getInstance()
                            timeCalendar.timeInMillis = selectedTime
                            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))

                            val expense = SimpleExpense(
                                title = title,
                                amount = amountValue,
                                currency = selectedCurrency, // Always USD
                                category = selectedCategory.label,
                                date = calendar.timeInMillis,
                                paymentMethod = paymentMethod,
                                description = description,
                                location = location
                            )

                            onSave(expense)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Expense")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
