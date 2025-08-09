package com.travelassistant.travella.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.travelassistant.travella.data.dao.ItineraryDao
import com.travelassistant.travella.data.dao.TripDao
import com.travelassistant.travella.data.model.ItineraryItem
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.FavoritePlace
import com.travelassistant.travella.data.dao.FavoritePlaceDao

@Database(
    entities = [TripItem::class, ItineraryItem::class, FavoritePlace::class],
    version = 3,                       // bumped from 2 -> 3
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun favoritePlaceDao(): FavoritePlaceDao

    companion object {
        @Volatile private var INSTANCE: TripDatabase? = null

        fun get(context: Context): TripDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TripDatabase::class.java,
                    "travella_trip_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
