package com.travelassistant.travella.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelassistant.travella.data.database.TripDatabase
import com.travelassistant.travella.data.model.TripItem
import com.travelassistant.travella.data.model.TripStatus
import com.travelassistant.travella.data.model.TripType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TripDaoTest {

    private lateinit var db: TripDatabase
    private lateinit var dao: TripDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TripDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.tripDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insert_and_getAll_returns_trip() = runBlocking {
        val t = TripItem(
            title = "Paris",
            destination = "France",
            startDate = 1000L,
            endDate = 2000L,
            status = TripStatus.PLANNED,
            type = TripType.LEISURE
        )
        val id = dao.insert(t)
        val list = dao.getAll().first()
        assertEquals(1, list.size)
        assertEquals(id, list.first().id.toLong())
        assertEquals("Paris", list.first().title)
    }
}
