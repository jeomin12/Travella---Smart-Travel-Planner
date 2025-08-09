// app/src/main/java/com/travelassistant/travella/ui/screens/MapScreen.kt
package com.travelassistant.travella.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.travelassistant.travella.viewmodel.MapViewModel
import com.travelassistant.travella.viewmodel.FavoritePlacesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    // pass a real tripId when navigating from TripDetail if you want the route for that trip
    tripId: Int? = null,
    mapViewModel: MapViewModel = viewModel(),
    favVm: FavoritePlacesViewModel = viewModel()
) {
    // Initial camera (can be anything sensible)
    val home = LatLng(-33.852, 151.211)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(home, 11f)
    }
    val scope = rememberCoroutineScope()

    // Map UI state
    var showLayers by remember { mutableStateOf(false) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val mapTypes = listOf(MapType.NORMAL, MapType.SATELLITE, MapType.TERRAIN, MapType.HYBRID)

    // Search
    var query by remember { mutableStateOf("") }
    val searchPin by mapViewModel.searchPin.collectAsState()

    // Favourites
    val favorites by favVm.favorites.collectAsState()
    var showFavs by remember { mutableStateOf(false) }

    // Load itinerary route for this screen
    LaunchedEffect(tripId) { mapViewModel.loadItineraryRoute(tripId) }
    val routePoints by mapViewModel.routePoints.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search places…") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (query.isNotBlank()) {
                                    mapViewModel.search(query)
                                }
                            }
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Save current place as favourite (search result if present, else camera target)
                    IconButton(onClick = {
                        val pin = searchPin ?: cameraPositionState.position.target
                        favVm.addFavorite(
                            name = if (query.isBlank()) "Saved place" else query,
                            lat = pin.latitude,
                            lng = pin.longitude
                        )
                    }) { Icon(Icons.Default.Star, contentDescription = "Save favourite") }

                    // Show favourites panel
                    IconButton(onClick = { showFavs = true }) {
                        Icon(Icons.Default.Bookmarks, contentDescription = "Show favourites")
                    }

                    // Map type menu
                    Box {
                        IconButton(onClick = { showLayers = !showLayers }) {
                            Icon(Icons.Default.Layers, contentDescription = "Map type")
                        }
                        DropdownMenu(expanded = showLayers, onDismissRequest = { showLayers = false }) {
                            mapTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        mapType = type
                                        showLayers = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {

            val mapProps = MapProperties(mapType = mapType)
            val uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = true
            )

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProps,
                uiSettings = uiSettings,
                onMapClick = { /* dismiss info panels if needed */ }
            ) {
                // Marker for search result
                searchPin?.let { pin ->
                    Marker(state = MarkerState(pin), title = "Search result")
                }
                // Draw itinerary route (sorted in ViewModel)
                if (routePoints.size >= 2) {
                    Polyline(
                        points = routePoints,
                        color = MaterialTheme.colorScheme.primary,
                        width = 12f
                    )
                    // Optional markers along the route
                    routePoints.forEachIndexed { idx, p ->
                        val title = when (idx) {
                            0 -> "Start"
                            routePoints.lastIndex -> "End"
                            else -> "Stop $idx"
                        }
                        Marker(state = MarkerState(p), title = title)
                    }
                }
            }

            // Animate camera on new search result
            LaunchedEffect(searchPin) {
                searchPin?.let { dest ->
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(dest, 14f),
                            durationMs = 500
                        )
                    }
                }
            }

            // Zoom / recenter controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.zoomIn(),
                                durationMs = 220
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) { Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in") }

                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.zoomOut(),
                                durationMs = 220
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) { Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out") }

                ExtendedFloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(home, 11f),
                                durationMs = 600
                            )
                        }
                    },
                    icon = { Icon(Icons.Default.CenterFocusStrong, contentDescription = "Recenter") },
                    text = { Text("Recenter") }
                )
            }

            // Simple favourites sheet
            if (showFavs) {
                ElevatedCard(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Favourites", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        favorites.forEach { place ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        val dest = LatLng(place.lat, place.lng)
                                        scope.launch {
                                            cameraPositionState.animate(
                                                update = CameraUpdateFactory.newLatLngZoom(dest, 14f),
                                                durationMs = 500
                                            )
                                        }
                                        showFavs = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(place.name, style = MaterialTheme.typography.bodyLarge)
                                    place.address?.takeIf { it.isNotBlank() }?.let {
                                        Text(
                                            it,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                IconButton(onClick = { favVm.deleteFavorite(place) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showFavs = false }) { Text("Close") }
                        }
                    }
                }
            }
        }
    }
}
