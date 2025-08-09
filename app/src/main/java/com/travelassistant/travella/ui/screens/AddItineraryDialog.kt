package com.travelassistant.travella.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelassistant.travella.data.model.ItineraryItem
import com.travelassistant.travella.data.model.ItineraryType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddItineraryDialog(
    tripId: Int,
    onDismiss: () -> Unit,
    onSave: (ItineraryItem) -> Unit
) {
    val context = LocalContext.current

    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var confirmationNumber by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ItineraryType.OTHER) }
    var showTypeDropdown by remember { mutableStateOf(false) }

    // Date and time state
    val calendar = Calendar.getInstance()
    var startDate by remember { mutableStateOf(calendar.timeInMillis) }
    var startTime by remember { mutableStateOf(calendar.timeInMillis) }
    var endDate by remember { mutableStateOf(calendar.timeInMillis) }
    var endTime by remember { mutableStateOf(calendar.apply { add(Calendar.HOUR, 2) }.timeInMillis) }

    // Date/Time formatters
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Validation state
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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
                    "Add Itinerary Item",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    supportingText = if (showError && title.isBlank()) {
                        { Text("Title is required") }
                    } else null
                )

                // Type Selection - Using regular dropdown instead of experimental
                Column {
                    Text(
                        "Item Type *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = getTypeDisplayName(selectedType),
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    if (showTypeDropdown) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            },
                            leadingIcon = {
                                Icon(getTypeIcon(selectedType), contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTypeDropdown = !showTypeDropdown }
                        )

                        DropdownMenu(
                            expanded = showTypeDropdown,
                            onDismissRequest = { showTypeDropdown = false }
                        ) {
                            ItineraryType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                getTypeIcon(type),
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(getTypeDisplayName(type))
                                        }
                                    },
                                    onClick = {
                                        selectedType = type
                                        showTypeDropdown = false
                                        // Auto-fill title based on type
                                        if (title.isBlank()) {
                                            title = getDefaultTitle(type)
                                        }
                                    }
                                )
                            }
                        }
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
                    maxLines = 3,
                    placeholder = { Text("Add details about this item...") }
                )

                // Date and Time Selection
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Date & Time",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        // Start Date and Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Start Date
                            OutlinedButton(
                                onClick = {
                                    val cal = Calendar.getInstance()
                                    cal.timeInMillis = startDate
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            val newCal = Calendar.getInstance()
                                            newCal.set(year, month, day)
                                            startDate = newCal.timeInMillis
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
                                    dateFormatter.format(Date(startDate)),
                                    fontSize = 12.sp
                                )
                            }

                            // Start Time
                            OutlinedButton(
                                onClick = {
                                    val cal = Calendar.getInstance()
                                    cal.timeInMillis = startTime
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            val newCal = Calendar.getInstance()
                                            newCal.timeInMillis = startDate
                                            newCal.set(Calendar.HOUR_OF_DAY, hour)
                                            newCal.set(Calendar.MINUTE, minute)
                                            startTime = newCal.timeInMillis
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
                                    timeFormatter.format(Date(startTime)),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Location Field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter address or venue name") }
                )

                // Confirmation Number (for bookings)
                if (selectedType == ItineraryType.FLIGHT || selectedType == ItineraryType.HOTEL) {
                    OutlinedTextField(
                        value = confirmationNumber,
                        onValueChange = { confirmationNumber = it },
                        label = { Text("Confirmation Number") },
                        leadingIcon = {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Booking reference or confirmation code") }
                    )
                }

                // Cost Field
                OutlinedTextField(
                    value = cost,
                    onValueChange = { newValue ->
                        // Only allow numbers and decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            cost = newValue
                        }
                    },
                    label = { Text("Cost") },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.00") },
                    prefix = { Text("$") }
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
                        else -> {
                            // Create the itinerary item
                            val costAmount = cost.toDoubleOrNull() ?: 0.0

                            // Combine date and time
                            val startCal = Calendar.getInstance()
                            startCal.timeInMillis = startDate
                            val timeCal = Calendar.getInstance()
                            timeCal.timeInMillis = startTime
                            startCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                            startCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))

                            val endCal = Calendar.getInstance()
                            endCal.timeInMillis = endDate
                            val endTimeCal = Calendar.getInstance()
                            endTimeCal.timeInMillis = endTime
                            endCal.set(Calendar.HOUR_OF_DAY, endTimeCal.get(Calendar.HOUR_OF_DAY))
                            endCal.set(Calendar.MINUTE, endTimeCal.get(Calendar.MINUTE))

                            val newItem = ItineraryItem(
                                tripId = tripId,
                                type = selectedType,
                                title = title,
                                description = description,
                                startTime = startCal.timeInMillis,
                                endTime = endCal.timeInMillis,
                                location = location,
                                confirmationNumber = confirmationNumber,
                                cost = costAmount
                            )

                            onSave(newItem)
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
                Text("Add to Itinerary")
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

// Helper functions for the dialog
private fun getTypeIcon(type: ItineraryType) = when (type) {
    ItineraryType.FLIGHT -> Icons.Default.Flight
    ItineraryType.HOTEL -> Icons.Default.Hotel
    ItineraryType.RESTAURANT -> Icons.Default.Restaurant
    ItineraryType.ACTIVITY -> Icons.Default.LocalActivity
    ItineraryType.TRANSPORT -> Icons.Default.DirectionsCar
    ItineraryType.MEETING -> Icons.Default.Business
    ItineraryType.OTHER -> Icons.Default.Event
}

private fun getTypeDisplayName(type: ItineraryType) = when (type) {
    ItineraryType.FLIGHT -> "Flight"
    ItineraryType.HOTEL -> "Hotel / Accommodation"
    ItineraryType.RESTAURANT -> "Restaurant / Dining"
    ItineraryType.ACTIVITY -> "Activity / Tour"
    ItineraryType.TRANSPORT -> "Transportation"
    ItineraryType.MEETING -> "Business Meeting"
    ItineraryType.OTHER -> "Other"
}

private fun getDefaultTitle(type: ItineraryType) = when (type) {
    ItineraryType.FLIGHT -> "Flight"
    ItineraryType.HOTEL -> "Hotel Check-in"
    ItineraryType.RESTAURANT -> "Dinner Reservation"
    ItineraryType.ACTIVITY -> "Activity"
    ItineraryType.TRANSPORT -> "Transportation"
    ItineraryType.MEETING -> "Business Meeting"
    ItineraryType.OTHER -> "Event"
}