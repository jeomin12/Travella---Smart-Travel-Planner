package com.travelassistant.travella.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.travelassistant.travella.data.database.TripDatabase
import com.travelassistant.travella.data.model.FavoritePlace
import com.travelassistant.travella.repository.FavoritePlacesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritePlacesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FavoritePlacesRepository(
        TripDatabase.get(app).favoritePlaceDao()
    )

    val favorites = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addFavorite(name: String, lat: Double, lng: Double, address: String? = null) {
        viewModelScope.launch {
            repo.save(FavoritePlace(name = name, lat = lat, lng = lng, address = address))
        }
    }

    fun deleteFavorite(place: FavoritePlace) {
        viewModelScope.launch { repo.delete(place) }
    }
}
