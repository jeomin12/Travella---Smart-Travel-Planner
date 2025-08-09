package com.travelassistant.travella.repository

import com.travelassistant.travella.data.dao.ExpenseDao
import com.travelassistant.travella.data.dao.CategoryTotal
import com.travelassistant.travella.data.model.ExpenseEntity
import com.travelassistant.travella.data.service.CurrencyService
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val dao: ExpenseDao,
    private val currencyService: CurrencyService
) {

    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()

    suspend fun insertExpense(expense: ExpenseEntity): Long {
        // Convert to USD for consistent statistics
        // The SimpleAddExpenseDialog now ensures amount is in USD, so direct use is fine.
        // If it were to accept other currencies, conversion would happen here.
        val amountInUSD = if (expense.currency != "USD") {
            currencyService.convertCurrency(expense.amount, expense.currency, "USD")
        } else {
            expense.amount
        }

        return dao.insertExpense(expense.copy(amountInUSD = amountInUSD))
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        val amountInUSD = if (expense.currency != "USD") {
            currencyService.convertCurrency(expense.amount, expense.currency, "USD")
        } else {
            expense.amount
        }

        dao.updateExpense(expense.copy(amountInUSD = amountInUSD))
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        dao.deleteExpense(expense)
    }

    fun getExpensesByTrip(tripId: Int): Flow<List<ExpenseEntity>> {
        return dao.getExpensesByTrip(tripId)
    }

    fun getGeneralExpenses(): Flow<List<ExpenseEntity>> {
        return dao.getGeneralExpenses()
    }

    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>> {
        return dao.getExpensesByCategory(category)
    }

    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>> {
        return dao.getExpensesByDateRange(startDate, endDate)
    }

    suspend fun getTotalExpenses(): Double {
        return dao.getTotalExpenses() ?: 0.0
    }

    suspend fun getTotalExpensesByTrip(tripId: Int): Double {
        return dao.getTotalExpensesByTrip(tripId) ?: 0.0
    }

    suspend fun getExpensesByCategory(): List<CategoryTotal> {
        return dao.getExpensesByCategory()
    }

    suspend fun getExpenseById(id: Int): ExpenseEntity? {
        return dao.getExpenseById(id)
    }

    suspend fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double {
        return currencyService.convertCurrency(amount, fromCurrency, toCurrency)
    }

    suspend fun getSupportedCurrencies(): List<String> {
        return currencyService.getSupportedCurrencies()
    }

    suspend fun refreshCurrencyRates() {
        currencyService.refreshRates()
    }
}
