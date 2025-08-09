package com.travelassistant.travella.repository


import com.travelassistant.travella.data.dao.FavoritePlaceDao
import com.travelassistant.travella.data.model.FavoritePlace
import kotlinx.coroutines.flow.Flow

class FavoritePlacesRepository(private val dao: FavoritePlaceDao) {
    fun getAll(): Flow<List<FavoritePlace>> = dao.getAll()
    suspend fun save(place: FavoritePlace): Long = dao.upsert(place)
    suspend fun delete(place: FavoritePlace) = dao.delete(place)
}
