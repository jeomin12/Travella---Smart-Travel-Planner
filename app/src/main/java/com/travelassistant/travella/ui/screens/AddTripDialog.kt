package com.travelassistant.travella.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.travelassistant.travella.data.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddTripDialog(
    onDismiss: () -> Unit,
    onSave: (TripItem) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TripType.LEISURE) }
    var showTypeDropdown by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    var startDate by remember { mutableStateOf(calendar.timeInMillis) }
    var endDate by remember { mutableStateOf(calendar.apply { add(Calendar.DAY_OF_MONTH, 7) }.timeInMillis) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Trip") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Trip Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Trip Type Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showTypeDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Trip Type: ${selectedType.name}")
                    }

                    DropdownMenu(
                        expanded = showTypeDropdown,
                        onDismissRequest = { showTypeDropdown = false }
                    ) {
                        TripType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    selectedType = type
                                    showTypeDropdown = false
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
                    Button(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val cal = Calendar.getInstance()
                                    cal.set(year, month, day)
                                    startDate = cal.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start Date")
                    }

                    Button(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val cal = Calendar.getInstance()
                                    cal.set(year, month, day)
                                    endDate = cal.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("End Date")
                    }
                }

                Text(
                    text = "Dates: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(startDate))} - ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(endDate))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && destination.isNotBlank()) {
                        val budgetAmount = budget.toDoubleOrNull() ?: 0.0
                        onSave(
                            TripItem(
                                title = title,
                                destination = destination,
                                startDate = startDate,
                                endDate = endDate,
                                status = TripStatus.PLANNED,
                                type = selectedType,
                                totalBudget = budgetAmount,
                                spentAmount = 0.0
                            )
                        )
                    }
                }
            ) {
                Text("Create Trip")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}