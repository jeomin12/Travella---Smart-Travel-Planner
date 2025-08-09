// app/src/main/java/com/travelassistant/travella/viewmodel/MapViewModel.kt
package com.travelassistant.travella.viewmodel

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.travelassistant.travella.data.database.ItineraryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = ItineraryDatabase.getDatabase(app).itineraryDao()

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

    private val _searchPin = MutableStateFlow<LatLng?>(null)
    val searchPin: StateFlow<LatLng?> = _searchPin

    private val geocoder by lazy { Geocoder(getApplication(), Locale.getDefault()) }

    /** Load itinerary items for a trip and geocode their .location into LatLngs (sorted by startTime). */
    fun loadItineraryRoute(tripId: Int? = null) {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                if (tripId != null) {
                    dao.getItineraryItemsForTrip(tripId).firstOrNull() ?: emptyList()
                } else {
                    emptyList() // you can add a DAO to fetch all if needed
                }
            }

            val points = withContext(Dispatchers.IO) {
                items
                    .sortedBy { it.startTime }
                    .mapNotNull { it.location.toLatLngOrNull() }
            }
            _routePoints.value = points
        }
    }

    /** Search a place name/address and pin it. */
    fun search(query: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { query.toLatLngOrNull() }
            _searchPin.value = result
        }
    }

    private fun String.toLatLngOrNull(): LatLng? {
        if (isBlank()) return null
        return try {
            val res = geocoder.getFromLocationName(this, 1)?.firstOrNull()
            res?.let { LatLng(it.latitude, it.longitude) }
        } catch (_: Exception) { null }
    }
}
