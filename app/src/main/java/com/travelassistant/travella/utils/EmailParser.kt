package com.travelassistant.travella.utils


import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

data class ParsedBooking(
    val title: String,
    val destination: String,
    val startMillis: Long?,
    val endMillis: Long?,
    val items: List<ParsedItem>
)

data class ParsedItem(
    val type: String,     // FLIGHT | HOTEL | ACTIVITY | OTHER
    val title: String,
    val notes: String,
    val startMillis: Long? = null,
    val endMillis: Long? = null,
    val confirmationNumber: String? = null, // Added confirmation number
    val location: String? = null, // Added location
    // New fields for more details from attachments
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
    val bookingReference: String? = null // General booking reference
)

object EmailParser {
    private const val datePattern =
        "(\\d{1,2}\\s+[A-Za-z]{3}\\s+\\d{4}|\\d{4}-\\d{2}-\\d{2}|[A-Za-z]{3}\\s+\\d{1,2},\\s+\\d{4}|\\d{2}/\\d{2}/\\d{4})"
    private const val timePattern = "(\\d{1,2}:\\d{2}\\s*(?:AM|PM)?)"

    private val dateFormats = listOf(
        SimpleDateFormat("d MMM yyyy", Locale.ENGLISH),
        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
        SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH),
        SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
    )

    private val timeFormats = listOf(
        SimpleDateFormat("HH:mm", Locale.ENGLISH),
        SimpleDateFormat("h:mm a", Locale.ENGLISH)
    )


    private fun parseDateToMillis(s: String?): Long? {
        val raw = s?.trim()?.takeIf { it.isNotBlank() } ?: return null
        for (fmt in dateFormats) {
            runCatching { return fmt.parse(raw)?.time }.onFailure { /* ignore */ }
        }
        return null
    }

    private fun parseDateTimeToMillis(dateStr: String?, timeStr: String?): Long? {
        val dateMillis = parseDateToMillis(dateStr) ?: return null
        val timeMillis = timeStr?.let { time ->
            for (fmt in timeFormats) {
                runCatching { return@let fmt.parse(time)?.time }.onFailure { /* ignore */ }
            }
            null
        } ?: return dateMillis // If no time, just return date

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        val timeCalendar = Calendar.getInstance()
        timeCalendar.timeInMillis = timeMillis

        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    // --- New function to simulate parsing from attachment content ---
    fun parseAttachmentContent(content: String): List<ParsedItem> {
        val items = mutableListOf<ParsedItem>()

        // Simulate parsing for a flight ticket
        val flightNumberMatch = Regex("Flight Number:\\s*([A-Z0-9]+)").find(content)
        val airlineMatch = Regex("Airline:\\s*(.+)").find(content)
        val departureMatch = Regex("Departure:\\s*(.+?)\\s+Date:\\s*($datePattern)\\s+Time:\\s*($timePattern)").find(content)
        val arrivalMatch = Regex("Arrival:\\s*(.+?)\\s+Date:\\s*($datePattern)\\s+Time:\\s*($timePattern)").find(content)
        val gateMatch = Regex("Gate:\\s*([A-Z0-9]+)").find(content)
        val terminalMatch = Regex("Terminal:\\s*([A-Z0-9]+)").find(content)
        val flightConfirmationMatch = Regex("Booking Reference:\\s*([A-Z0-9]+)").find(content)

        if (flightNumberMatch != null && departureMatch != null && arrivalMatch != null) {
            val flightNumber = flightNumberMatch.groupValues[1]
            val airline = airlineMatch?.groupValues?.get(1)
            val departureLocation = departureMatch.groupValues[1]
            val departureDate = departureMatch.groupValues[2]
            val departureTime = departureMatch.groupValues[3]
            val arrivalLocation = arrivalMatch.groupValues[1]
            val arrivalDate = arrivalMatch.groupValues[2]
            val arrivalTime = arrivalMatch.groupValues[3]
            val gate = gateMatch?.groupValues?.get(1)
            val terminal = terminalMatch?.groupValues?.get(1)
            val confirmation = flightConfirmationMatch?.groupValues?.get(1)

            items.add(
                ParsedItem(
                    type = "FLIGHT",
                    title = "Flight $flightNumber",
                    notes = "From $departureLocation to $arrivalLocation",
                    startMillis = parseDateTimeToMillis(departureDate, departureTime),
                    endMillis = parseDateTimeToMillis(arrivalDate, arrivalTime),
                    airline = airline,
                    flightNumber = flightNumber,
                    gate = gate,
                    terminal = terminal,
                    location = "$departureLocation - $arrivalLocation",
                    bookingReference = confirmation
                )
            )
        }

        // Simulate parsing for a hotel booking
        val hotelNameMatch = Regex("Hotel Name:\\s*(.+)").find(content)
        val hotelCheckInMatch = Regex("Check-in:\\s*($datePattern)\\s+($timePattern)").find(content)
        val hotelCheckOutMatch = Regex("Check-out:\\s*($datePattern)\\s+($timePattern)").find(content)
        val hotelAddressMatch = Regex("Address:\\s*(.+)").find(content)
        val roomNumberMatch = Regex("Room Number:\\s*(.+)").find(content)
        val hotelConfirmationMatch = Regex("Confirmation Number:\\s*([A-Z0-9]+)").find(content)

        if (hotelNameMatch != null && hotelCheckInMatch != null && hotelCheckOutMatch != null) {
            val hotelName = hotelNameMatch.groupValues[1]
            val checkInDate = hotelCheckInMatch.groupValues[1]
            val checkInTime = hotelCheckInMatch.groupValues[2]
            val checkOutDate = hotelCheckOutMatch.groupValues[1]
            val checkOutTime = hotelCheckOutMatch.groupValues[2]
            val address = hotelAddressMatch?.groupValues?.get(1)
            val roomNumber = roomNumberMatch?.groupValues?.get(1)
            val confirmation = hotelConfirmationMatch?.groupValues?.get(1)

            items.add(
                ParsedItem(
                    type = "HOTEL",
                    title = hotelName,
                    notes = "Stay at $hotelName",
                    startMillis = parseDateTimeToMillis(checkInDate, checkInTime),
                    endMillis = parseDateTimeToMillis(checkOutDate, checkOutTime),
                    hotelName = hotelName,
                    roomNumber = roomNumber,
                    checkInDate = parseDateToMillis(checkInDate),
                    checkOutDate = parseDateToMillis(checkOutDate),
                    location = address,
                    confirmationNumber = confirmation,
                    bookingReference = confirmation
                )
            )
        }

        // Add more parsing logic for other types (activities, car rentals, etc.)

        return items
    }

    // Original parse function for email body
    fun parse(rawEmail: String): ParsedBooking {
        val text = rawEmail.replace("\r", "")

        val title = Regex("Subject:\\s*(.*)", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.getOrNull(1)?.takeIf { it.isNotBlank() }
            ?: "Imported Trip"

        val destination =
            Regex("Destination[: ]+(.*)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)?.trim()
                ?: Regex("To[: ]+([A-Z]{3})", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)?.trim()
                ?: ""

        val checkIn  = Regex("Check[- ]?in[: ]+(.+)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)
        val checkOut = Regex("Check[- ]?out[: ]+(.+)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)
        val depart   = Regex("Depart(?:ure)?[: ]+(.+)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)
        val `return` = Regex("Return[: ]+(.+)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)

        val start = parseDateToMillis(checkIn ?: depart)
        val end   = parseDateToMillis(checkOut ?: `return`)

        val items = mutableListOf<ParsedItem>()

        // Flight details
        val flightRegex = Regex("Flight(?: Number)?:\\s*([A-Z0-9]+)\\s*(?:from\\s*(.*?)\\s*to\\s*(.*?))?\\s*(?:on\\s*($datePattern))?\\s*(?:at\\s*($timePattern))?", RegexOption.IGNORE_CASE)
        // Hotel details
        val hotelRegex = Regex("Hotel(?: Name)?:\\s*(.*?)\\s*(?:Check-in:\\s*($datePattern)(?:\\s*($timePattern))?)?\\s*(?:Check-out:\\s*($datePattern)(?:\\s*($timePattern))?)?\\s*(?:Confirmation(?: Number)?:\\s*([A-Z0-9]+))?\\s*(?:Address:\\s*(.*?))?", RegexOption.IGNORE_CASE)
        // Activity details
        val activityRegex = Regex("Activity(?: Name)?:\\s*(.*?)\\s*(?:Date:\\s*($datePattern)(?:\\s*($timePattern))?)?\\s*(?:Location:\\s*(.*?))?", RegexOption.IGNORE_CASE)

        // Parse Flights
        flightRegex.findAll(text).forEach { match ->
            val flightNumber = match.groupValues[1].trim()
            val fromLocation = match.groupValues[2].trim()
            val toLocation = match.groupValues[3].trim()
            val date = match.groupValues[4].trim()
            val time = match.groupValues[5].trim()

            val startFlight = parseDateTimeToMillis(date, time)

            items += ParsedItem(
                type = "FLIGHT",
                title = "Flight $flightNumber to $toLocation",
                notes = "From: $fromLocation, On: $date $time",
                startMillis = startFlight,
                location = "$fromLocation to $toLocation",
                flightNumber = flightNumber,
                airline = null // Can be extracted with more complex regex
            )
        }

        // Parse Hotels
        hotelRegex.findAll(text).forEach { match ->
            val hotelName = match.groupValues[1].trim()
            val checkInDate = match.groupValues[2].trim()
            val checkInTime = match.groupValues[3].trim()
            val checkOutDate = match.groupValues[4].trim()
            val checkOutTime = match.groupValues[5].trim()
            val confirmation = match.groupValues[6].trim()
            val address = match.groupValues[7].trim()

            val startHotel = parseDateTimeToMillis(checkInDate, checkInTime)
            val endHotel = parseDateTimeToMillis(checkOutDate, checkOutTime)

            items += ParsedItem(
                type = "HOTEL",
                title = hotelName,
                notes = "Check-in: $checkInDate $checkInTime, Check-out: $checkOutDate $checkOutTime",
                startMillis = startHotel,
                endMillis = endHotel,
                confirmationNumber = confirmation,
                location = address,
                hotelName = hotelName,
                checkInDate = parseDateToMillis(checkInDate),
                checkOutDate = parseDateToMillis(checkOutDate)
            )
        }

        // Parse Activities
        activityRegex.findAll(text).forEach { match ->
            val activityName = match.groupValues[1].trim()
            val date = match.groupValues[2].trim()
            val time = match.groupValues[3].trim()
            val location = match.groupValues[4].trim()

            val startActivity = parseDateTimeToMillis(date, time)

            items += ParsedItem(
                type = "ACTIVITY",
                title = activityName,
                notes = "On: $date $time",
                startMillis = startActivity,
                location = location,
                activityName = activityName
            )
        }

        // Fallback if no specific items are found
        if (items.isEmpty()) {
            items += ParsedItem(type = "OTHER", title = "Email import", notes = text.take(300))
        }

        // Determine overall trip dates from parsed items if not found directly
        val overallStart = items.mapNotNull { it.startMillis }.minOrNull()
        val overallEnd = items.mapNotNull { it.endMillis ?: it.startMillis }.maxOrNull()

        return ParsedBooking(
            title = title,
            destination = destination,
            startMillis = overallStart,
            endMillis = overallEnd,
            items = items
        )
    }
}
