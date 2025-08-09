package com.travelassistant.travella.repository

import com.travelassistant.travella.data.dao.ExpenseDao
import com.travelassistant.travella.data.model.ExpenseEntity
import com.travelassistant.travella.data.service.CurrencyService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseRepositoryTest {

    private val dao: ExpenseDao = mockk(relaxed = true)
    private val currency: CurrencyService = mockk()
    private val repo = ExpenseRepository(dao, currency)

    @Test
    fun `insertExpense converts non-USD to USD before saving`() = runTest {
        // Given an expense in EUR
        val eurExpense = ExpenseEntity(
            id = 0,
            title = "Lunch",
            amount = 10.0,
            currency = "EUR",
            amountInUSD = 0.0, // repo fills this
            category = "Food",
            date = 111L,
            paymentMethod = "Cash"
        )

        coEvery { currency.convertCurrency(10.0, "EUR", "USD") } returns 11.0
        coEvery { dao.insertExpense(any()) } returns 1L

        // When
        val id = repo.insertExpense(eurExpense)

        // Then
        assertEquals(1L, id)
        coVerify {
            dao.insertExpense(
                match { it.amountInUSD == 11.0 && it.currency == "EUR" && it.title == "Lunch" }
            )
        }
    }

    @Test
    fun `updateExpense converts non-USD to USD before updating`() = runTest {
        val expense = ExpenseEntity(
            id = 12,
            title = "Taxi",
            amount = 20.0,
            currency = "GBP",
            amountInUSD = 0.0,
            category = "Transport",
            date = 222L,
            paymentMethod = "Card"
        )

        coEvery { currency.convertCurrency(20.0, "GBP", "USD") } returns 25.0

        repo.updateExpense(expense)

        coVerify {
            dao.updateExpense(
                match { it.id == 12 && it.amountInUSD == 25.0 }
            )
        }
    }
}
