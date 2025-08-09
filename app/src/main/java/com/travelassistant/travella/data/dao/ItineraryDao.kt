package com.travelassistant.travella.data.dao


import androidx.room.*
import com.travelassistant.travella.data.model.ItineraryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryItem(item: ItineraryItem)

    @Update
    suspend fun updateItineraryItem(item: ItineraryItem)

    @Delete
    suspend fun deleteItineraryItem(item: ItineraryItem)

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId ORDER BY startTime ASC")
    fun getItineraryItemsForTrip(tripId: Int): Flow<List<ItineraryItem>>

    @Query("SELECT * FROM itinerary_items WHERE id = :itemId LIMIT 1")
    suspend fun getItineraryItemById(itemId: Int): ItineraryItem?

    @Query("DELETE FROM itinerary_items WHERE tripId = :tripId")
    suspend fun deleteItineraryItemsByTrip(tripId: Int)
}
