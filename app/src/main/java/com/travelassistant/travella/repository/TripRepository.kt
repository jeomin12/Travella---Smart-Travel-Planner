package com.travelassistant.travella.repository



import com.travelassistant.travella.data.dao.TripDao
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.TripStatus
import kotlinx.coroutines.flow.Flow

class TripRepository(private val dao: TripDao) {
    fun getAll(): Flow<List<TripItem>> = dao.getAll()
    fun getById(id: Int): Flow<TripItem?> = dao.getById(id)
    fun getUpcoming(now: Long): Flow<List<TripItem>> = dao.getUpcoming(now)
    fun getByStatus(status: TripStatus): Flow<List<TripItem>> = dao.getByStatus(status)

    suspend fun add(trip: TripItem): Long = dao.insert(trip)
    suspend fun update(trip: TripItem) = dao.update(trip)
    suspend fun delete(trip: TripItem) = dao.delete(trip)
    suspend fun updateSpent(tripId: Int, spent: Double) = dao.updateSpent(tripId, spent)
}
