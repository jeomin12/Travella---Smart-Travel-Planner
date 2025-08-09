package com.travelassistant.travella.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_places")
data class FavoritePlace(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String? = null,
    val lat: Double,
    val lng: Double,
    val notes: String? = null
)
