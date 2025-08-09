package com.travelassistant.travella.data.dao

import androidx.room.*
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.TripStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: TripItem): Long

    @Update
    suspend fun update(trip: TripItem)

    @Delete
    suspend fun delete(trip: TripItem)

    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAll(): Flow<List<TripItem>>

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<TripItem?>

    @Query("SELECT * FROM trips WHERE startDate >= :now ORDER BY startDate ASC")
    fun getUpcoming(now: Long): Flow<List<TripItem>>

    @Query("SELECT * FROM trips WHERE status = :status ORDER BY startDate DESC")
    fun getByStatus(status: TripStatus): Flow<List<TripItem>>

    @Query("UPDATE trips SET spentAmount = :spent WHERE id = :tripId")
    suspend fun updateSpent(tripId: Int, spent: Double)
}
