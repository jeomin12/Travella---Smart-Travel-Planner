package com.travelassistant.travella.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itinerary_items")
data class ItineraryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tripId: Int,
    val type: ItineraryType,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val location: String = "",
    val confirmationNumber: String = "",
    val cost: Double = 0.0,
    val status: String = "Confirmed",
    // New fields for more details
    val airline: String? = null,
    val flightNumber: String? = null,
    val gate: String? = null,
    val terminal: String? = null,
    val hotelName: String? = null,
    val roomNumber: String? = null,
    val checkInDate: Long? = null,
    val checkOutDate: Long? = null,
    val activityName: String? = null,
    val activityDuration: String? = null,
    val bookingReference: String? = null,
    val attachmentUri: String? = null
)

enum class ItineraryType {
    FLIGHT, HOTEL, RESTAURANT, ACTIVITY, TRANSPORT, MEETING, OTHER
}
