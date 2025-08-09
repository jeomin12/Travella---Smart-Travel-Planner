package com.travelassistant.travella.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.travelassistant.travella.data.database.TripDatabase
import com.travelassistant.travella.data.database.ItineraryDatabase
import com.travelassistant.travella.data.model.ItineraryItem
import com.travelassistant.travella.data.model.ItineraryType
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.TripStatus
import com.travelassistant.travella.data.model.TripType
import com.travelassistant.travella.repository.TripRepository
import com.travelassistant.travella.repository.ItineraryRepository
import com.travelassistant.travella.utils.EmailParser
import com.travelassistant.travella.utils.ParsedItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

enum class TripFilter { ALL, UPCOMING, IN_PROGRESS, COMPLETED }

class TripDashboardViewModel(app: Application) : AndroidViewModel(app) {

    // Repos (single instances — no duplicates)
    private val tripRepo = TripRepository(TripDatabase.get(app).tripDao())
    private val itineraryRepo = ItineraryRepository(
        ItineraryDatabase.getDatabase(app).itineraryDao()
    )

    // UI filter
    private val _selectedFilter = MutableStateFlow(TripFilter.ALL)
    val selectedFilter: StateFlow<TripFilter> = _selectedFilter.asStateFlow()

    // Base stream from DB
    private val allTripsFlow: Flow<List<TripItem>> = tripRepo.getAll()

    // Exposed, filtered trips
    val allTrips: StateFlow<List<TripItem>> =
        combine(allTripsFlow, _selectedFilter) { trips, filter ->
            when (filter) {
                TripFilter.ALL -> trips
                TripFilter.UPCOMING -> {
                    val now = System.currentTimeMillis()
                    trips.filter { it.startDate >= now && it.status != TripStatus.CANCELLED }
                }
                TripFilter.IN_PROGRESS -> {
                    val now = System.currentTimeMillis()
                    trips.filter { now in it.startDate..it.endDate }
                }
                TripFilter.COMPLETED -> trips.filter { it.status == TripStatus.COMPLETED }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setFilter(filter: TripFilter) { _selectedFilter.value = filter }

    fun addTrip(trip: TripItem) = viewModelScope.launch { tripRepo.add(trip) }

    fun updateTrip(trip: TripItem) = viewModelScope.launch { tripRepo.update(trip) }

    fun deleteTrip(trip: TripItem) = viewModelScope.launch { tripRepo.delete(trip) }

    fun getTripById(id: Int): StateFlow<TripItem?> =
        tripRepo.getById(id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Parses email body (and optional attachment text) into a Trip and ItineraryItems, and saves them.
     * @param emailContent raw email text (subject/body)
     * @param attachmentContent optional plain-text extracted from an attachment (e.g., PDF -> text)
     * @param attachmentUri optional URI string to store for traceability
     */
    fun importBookingFromEmail(
        emailContent: String,
        attachmentContent: String? = null,
        attachmentUri: String? = null
    ) = viewModelScope.launch {
        // 1) Parse email
        val parsedBooking = EmailParser.parse(emailContent)

        // 2) Parse attachment if provided (returns ParsedItem list; may be empty)
        val parsedFromAttachment: List<ParsedItem> =
            attachmentContent?.let { EmailParser.parseAttachmentContent(it) } ?: emptyList()

        // Merge items (email first, then attachment-derived)
        val allParsedItems: List<ParsedItem> = parsedBooking.items + parsedFromAttachment

        // 3) Build & insert Trip
        val newTrip = TripItem(
            title = parsedBooking.title.ifBlank { "Imported Trip" },
            destination = parsedBooking.destination,
            startDate = parsedBooking.startMillis ?: System.currentTimeMillis(),
            endDate = parsedBooking.endMillis ?: (System.currentTimeMillis() + 86_400_000L),
            status = TripStatus.CONFIRMED,
            type = TripType.LEISURE,        // default; you can infer type if needed
            totalBudget = 0.0,
            spentAmount = 0.0,
            imageUrl = "",
            notes = "Imported from email",
            isCompleted = false
        )
        val tripId = tripRepo.add(newTrip).toInt()

        // 4) Map ParsedItem -> ItineraryItem and insert
        allParsedItems.forEach { parsed ->
            val (type, title, notes, start, end) = parsed
            val itineraryType = when (type.uppercase(Locale.getDefault())) {
                "FLIGHT" -> ItineraryType.FLIGHT
                "HOTEL" -> ItineraryType.HOTEL
                "ACTIVITY" -> ItineraryType.ACTIVITY
                "TRANSPORT" -> ItineraryType.TRANSPORT
                "MEETING" -> ItineraryType.MEETING
                "RESTAURANT" -> ItineraryType.RESTAURANT
                else -> ItineraryType.OTHER
            }

            val item = ItineraryItem(
                tripId = tripId,
                type = itineraryType,
                title = title.ifBlank { itineraryType.name },
                description = notes,
                startTime = start ?: newTrip.startDate,
                endTime = end ?: newTrip.endDate,
                location = parsed.location.orEmpty(),
                confirmationNumber = parsed.confirmationNumber.orEmpty(),
                status = "Confirmed",
                // enriched fields from attachment (present in your model)
                airline = parsed.airline,
                flightNumber = parsed.flightNumber,
                gate = parsed.gate,
                terminal = parsed.terminal,
                hotelName = parsed.hotelName,
                roomNumber = parsed.roomNumber,
                checkInDate = parsed.checkInDate,
                checkOutDate = parsed.checkOutDate,
                activityName = parsed.activityName,
                activityDuration = parsed.activityDuration,
                bookingReference = parsed.bookingReference,
                attachmentUri = attachmentUri
            )

            itineraryRepo.insertItineraryItem(item)
        }
    }
}
