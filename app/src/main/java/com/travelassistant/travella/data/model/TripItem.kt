package com.travelassistant.travella.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val status: TripStatus,
    val type: TripType,
    val totalBudget: Double = 0.0,
    val spentAmount: Double = 0.0,
    val imageUrl: String = "",
    val notes: String = "",
    val isCompleted: Boolean = false
)

enum class TripStatus {
    PLANNED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class TripType {
    BUSINESS, LEISURE, FAMILY, ADVENTURE, ROMANTIC, SOLO
}