package com.travelassistant.travella.ui.screens

import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.ItineraryItem
import com.travelassistant.travella.data.model.ItineraryType
import com.travelassistant.travella.ui.components.ItineraryItemCard // Re-using existing component
import com.travelassistant.travella.viewmodel.TripDashboardViewModel
import com.travelassistant.travella.repository.ItineraryRepository // Import ItineraryRepository
import com.travelassistant.travella.data.database.ItineraryDatabase // Import ItineraryDatabase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    navController: NavHostController,
    tripId: Int,
    viewModel: TripDashboardViewModel
) {
    // Room-backed: returns StateFlow<TripItem?>
    val trip: TripItem? by viewModel.getTripById(tripId).collectAsState()

    // Fetch itinerary items for this trip
    val context = LocalContext.current
    val itineraryRepository = remember(context) {
        ItineraryRepository(ItineraryDatabase.getDatabase(context).itineraryDao())
    }
    val itineraryItems by itineraryRepository.getItineraryItemsForTrip(tripId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.title ?: "Trip details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (trip == null) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(Modifier.padding(24.dp))
            }
            return@Scaffold
        }

        val df = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
        val t = trip!! // safe now

        LazyColumn( // Changed to LazyColumn for scrollability and efficiency
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp), // Apply horizontal padding here
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp) // Add vertical padding for content
        ) {
            item {
                Text(t.destination, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(t.title, style = MaterialTheme.typography.titleMedium)
            }

            // Dates
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.Event, contentDescription = null)
                        Column {
                            Text("Dates", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text("${df.format(Date(t.startDate))} — ${df.format(Date(t.endDate))}")
                        }
                    }
                }
            }

            // Status / Type
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.Flag, contentDescription = null)
                        Column {
                            Text("Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(t.status.name.lowercase().replaceFirstChar { it.titlecase() })
                            Spacer(Modifier.height(8.dp))
                            Text("Type", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(t.type.name.lowercase().replaceFirstChar { it.titlecase() })
                        }
                    }
                }
            }

            // Budget
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                        Column {
                            Text("Budget", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text("Total: $${t.totalBudget}")
                            Text("Spent: $${t.spentAmount}")
                        }
                    }
                }
            }

            // Itinerary Items Section
            item {
                Text(
                    "Itinerary Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            if (itineraryItems.isEmpty()) {
                item {
                    Text(
                        "No itinerary items found for this trip.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(itineraryItems) { item ->
                    ItineraryItemCard(
                        item = item,
                        onEdit = { /* TODO: Implement edit functionality */ },
                        onDelete = { /* TODO: Implement delete functionality */ }
                    )
                }
            }
        }
    }
}
