package com.travelassistant.travella.data.dao

import androidx.room.*
import com.travelassistant.travella.data.model.FavoritePlace
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlaceDao {
    @Query("SELECT * FROM favorite_places ORDER BY name ASC")
    fun getAll(): Flow<List<FavoritePlace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(place: FavoritePlace): Long

    @Delete
    suspend fun delete(place: FavoritePlace)
}
