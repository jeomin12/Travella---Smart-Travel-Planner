// FILE: app/src/main/java/com/travelassistant/travella/ui/screens/TripDashboardScreen.kt
package com.travelassistant.travella.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
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
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.TripStatus
import com.travelassistant.travella.data.model.TripType
import com.travelassistant.travella.ui.components.FilterChip
import com.travelassistant.travella.ui.components.ModernTripCard
import com.travelassistant.travella.ui.components.QuickStatsCard
import com.travelassistant.travella.viewmodel.TripDashboardViewModel
import com.travelassistant.travella.viewmodel.TripFilter
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TripDashboardScreen(
    navController: NavHostController,
    viewModel: TripDashboardViewModel
) {
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddTripDialog by remember { mutableStateOf(false) }

    val allTrips by viewModel.allTrips.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Filter by status
    val statusFiltered = remember(allTrips, selectedFilter) {
        allTrips.filter { trip ->
            when (selectedFilter) {
                TripFilter.UPCOMING -> trip.status != TripStatus.CANCELLED && trip.status != TripStatus.COMPLETED
                TripFilter.COMPLETED -> trip.status == TripStatus.COMPLETED
                TripFilter.IN_PROGRESS -> trip.status == TripStatus.IN_PROGRESS
                TripFilter.ALL -> true
            }
        }
    }
    // Search
    val filteredTrips = remember(statusFiltered, searchQuery) {
        val q = searchQuery.trim().lowercase(Locale.getDefault())
        if (q.isEmpty()) statusFiltered
        else statusFiltered.filter {
            it.title.lowercase(Locale.getDefault()).contains(q) ||
                    it.destination.lowercase(Locale.getDefault()).contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Travels", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Explore • Plan • Experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    // REMOVED the top Import Email button (was here before)
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            // Stack two FABs near the bottom-right: Import Email above New Trip
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("importEmail") },
                    icon = { Icon(Icons.Default.Email, contentDescription = "Import Email") },
                    text = { Text("Import Email") }
                )
                ExtendedFloatingActionButton(
                    onClick = { showAddTripDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "New Trip") },
                    text = { Text("New Trip") }
                )

            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (showSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search by title or destination") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
            }

            WelcomeBanner()
            Spacer(Modifier.height(16.dp))

            QuickStatsSection(trips = allTrips)
            Spacer(Modifier.height(16.dp))

            FilterSection(
                selectedFilter = selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )
            Spacer(Modifier.height(12.dp))

            if (filteredTrips.isEmpty()) {
                EmptyState(filter = selectedFilter)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredTrips,
                        key = { trip -> trip.id }
                    ) { trip ->
                        // SWIPE-TO-DELETE WRAPPER
                        val dismissState = rememberDismissState(
                            confirmStateChange = { value ->
                                if (value == DismissValue.DismissedToStart || value == DismissValue.DismissedToEnd) {
                                    // Perform delete
                                    viewModel.deleteTrip(trip)
                                    // Offer UNDO
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Trip deleted",
                                            actionLabel = "Undo",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            // Re-add (new id will be generated)
                                            viewModel.addTrip(trip.copy(id = 0))
                                        }
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                // Red delete background
                                val bgColor = when (dismissState.dismissDirection) {
                                    DismissDirection.EndToStart -> Color(0xFFE53935)
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min)
                                        .background(bgColor)
                                        .padding(end = 24.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            },
                            dismissContent = {
                                ModernTripCard(
                                    trip = trip,
                                    onTripClick = { navController.navigate("tripDetail/${trip.id}") },
                                    onAddItinerary = { /* no-op */ }
                                )
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showAddTripDialog) {
        AddTripDialog(
            onDismiss = { showAddTripDialog = false },
            onSave = { trip ->
                viewModel.addTrip(trip)
                showAddTripDialog = false
            }
        )
    }
}

/* ----------------- Helpers (unchanged) ----------------- */

@Composable
private fun WelcomeBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    "Ready for your next adventure?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Plan, organize, and track your travels",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
            Icon(
                Icons.Default.Flight, contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd).size(40.dp),
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun QuickStatsSection(trips: List<TripItem>) {
    val upcomingTrips = trips.count { !it.isCompleted && it.status != TripStatus.CANCELLED }
    val completedTrips = trips.count { it.isCompleted }
    val totalSpent = trips.sumOf { it.spentAmount }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatsCard(
            title = "Upcoming",
            value = upcomingTrips.toString(),
            icon = Icons.Default.Schedule,
            backgroundColor = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        QuickStatsCard(
            title = "Completed",
            value = completedTrips.toString(),
            icon = Icons.Default.CheckCircle,
            backgroundColor = Color(0xFF2196F3),
            modifier = Modifier.weight(1f)
        )
        QuickStatsCard(
            title = "Total Spent",
            value = "$${totalSpent.toInt()}",
            icon = Icons.Default.AttachMoney,
            backgroundColor = Color(0xFF9C27B0),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FilterSection(
    selectedFilter: TripFilter,
    onFilterSelected: (TripFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(TripFilter.values()) { filter ->
            FilterChip(
                label = when (filter) {
                    TripFilter.ALL -> "All Trips"
                    TripFilter.UPCOMING -> "Upcoming"
                    TripFilter.IN_PROGRESS -> "In Progress"
                    TripFilter.COMPLETED -> "Completed"
                },
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun EmptyState(filter: TripFilter) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Luggage,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (filter) {
                    TripFilter.ALL -> "No trips yet"
                    TripFilter.UPCOMING -> "No upcoming trips"
                    TripFilter.IN_PROGRESS -> "No trips in progress"
                    TripFilter.COMPLETED -> "No completed trips"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start planning your next adventure!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
