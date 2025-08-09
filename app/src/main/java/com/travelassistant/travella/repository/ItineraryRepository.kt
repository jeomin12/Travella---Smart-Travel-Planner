package com.travelassistant.travella.repository

import com.travelassistant.travella.data.dao.ItineraryDao
import com.travelassistant.travella.data.model.ItineraryItem
import kotlinx.coroutines.flow.Flow

class ItineraryRepository(private val dao: ItineraryDao) {
    suspend fun insertItineraryItem(item: ItineraryItem) = dao.insertItineraryItem(item)
    suspend fun updateItineraryItem(item: ItineraryItem) = dao.updateItineraryItem(item)
    suspend fun deleteItineraryItem(item: ItineraryItem) = dao.deleteItineraryItem(item)
    fun getItineraryItemsForTrip(tripId: Int): Flow<List<ItineraryItem>> = dao.getItineraryItemsForTrip(tripId)
    suspend fun getItineraryItemById(itemId: Int): ItineraryItem? = dao.getItineraryItemById(itemId)
    suspend fun deleteItineraryItemsByTrip(tripId: Int) = dao.deleteItineraryItemsByTrip(tripId)
}
