package com.travelassistant.travella.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.TripStatus
import com.travelassistant.travella.data.model.TripType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModernTripCard(
    trip: TripItem,
    onTripClick: () -> Unit,
    onAddItinerary: () -> Unit,   // kept for compatibility; not used
    modifier: Modifier = Modifier
) {
    val dateFmt = SimpleDateFormat("MMM dd", Locale.getDefault())  // no remember needed

    val statusColor = when (trip.status) {
        TripStatus.PLANNED -> Color(0xFF90CAF9)
        TripStatus.CONFIRMED -> Color(0xFF4CAF50)
        TripStatus.IN_PROGRESS -> Color(0xFFFFB300)
        TripStatus.COMPLETED -> Color(0xFF7E57C2)
        TripStatus.CANCELLED -> Color(0xFFE57373)
    }

    val gradient = when (trip.type) {
        TripType.BUSINESS -> listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
        TripType.LEISURE -> listOf(Color(0xFF56ab2f), Color(0xFFa8e063))
        TripType.FAMILY -> listOf(Color(0xFFee9ca7), Color(0xFFffdde1))
        TripType.ADVENTURE -> listOf(Color(0xFF00c6ff), Color(0xFF0072ff))
        TripType.ROMANTIC -> listOf(Color(0xFFf857a6), Color(0xFFff5858))
        TripType.SOLO -> listOf(Color(0xFF667eea), Color(0xFF764ba2))
    }

    val budgetProgress =
        if (trip.totalBudget > 0.0) (trip.spentAmount / trip.totalBudget).coerceIn(0.0, 1.0) else 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTripClick() }, // whole card opens details
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(gradient))
                    .padding(16.dp)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = trip.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (trip.destination.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = trip.destination,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${dateFmt.format(Date(trip.startDate))} — ${dateFmt.format(Date(trip.endDate))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 0.dp,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = trip.status.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }

            // Budget (optional)
            if (trip.totalBudget > 0.0) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "Budget: $${trip.spentAmount.toInt()} / $${trip.totalBudget.toInt()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = budgetProgress.toFloat(), // <-- value (not lambda)
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            // Actions: ONLY View Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onTripClick) {
                    Text("View Details")
                }
            }
        }
    }
}
