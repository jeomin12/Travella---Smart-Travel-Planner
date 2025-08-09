package com.travelassistant.travella.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelassistant.travella.data.database.ExpenseDatabase
import com.travelassistant.travella.data.model.ExpenseEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {

    private lateinit var db: ExpenseDatabase
    private lateinit var dao: ExpenseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ExpenseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.expenseDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insert_and_sum_total_works() = runBlocking {
        dao.insertExpense(
            ExpenseEntity(
                title = "Coffee",
                amount = 3.0,
                currency = "USD",
                amountInUSD = 3.0,
                category = "Food",
                date = 1L,
                paymentMethod = "Cash"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                title = "Bus",
                amount = 2.0,
                currency = "USD",
                amountInUSD = 2.0,
                category = "Transport",
                date = 2L,
                paymentMethod = "Card"
            )
        )

        val total = dao.getTotalExpenses()
        assertEquals(5.0, total!!, 0.001)

        val list = dao.getAllExpenses().first()
        assertEquals(2, list.size)
    }
}
