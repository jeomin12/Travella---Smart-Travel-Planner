package com.travelassistant.travella.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.travelassistant.travella.data.dao.ItineraryDao
import com.travelassistant.travella.data.model.ItineraryItem
import androidx.room.TypeConverters

@Database(entities = [ItineraryItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // <-- add this line
abstract class ItineraryDatabase : RoomDatabase(){
    abstract fun itineraryDao(): ItineraryDao

    companion object {
        @Volatile
        private var INSTANCE: ItineraryDatabase? = null

        fun getDatabase(context: Context): ItineraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItineraryDatabase::class.java,
                    "itinerary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
