package com.travelassistant.travella.data.database

import androidx.room.TypeConverter
import com.travelassistant.travella.data.model.TripStatus
import com.travelassistant.travella.data.model.TripType
import com.travelassistant.travella.data.model.ItineraryType

class Converters {
    @TypeConverter fun toStatus(value: String): TripStatus = TripStatus.valueOf(value)
    @TypeConverter fun fromStatus(value: TripStatus): String = value.name

    @TypeConverter fun toType(value: String): TripType = TripType.valueOf(value)
    @TypeConverter fun fromType(value: TripType): String = value.name

    class Converters {
        @TypeConverter fun toStatus(value: String) = TripStatus.valueOf(value)
        @TypeConverter fun fromStatus(value: TripStatus) = value.name

        @TypeConverter fun toType(value: String) = TripType.valueOf(value)
        @TypeConverter fun fromType(value: TripType) = value.name

        // NEW
        @TypeConverter fun toItinType(value: String) = ItineraryType.valueOf(value)
        @TypeConverter fun fromItinType(value: ItineraryType) = value.name
    }
}
