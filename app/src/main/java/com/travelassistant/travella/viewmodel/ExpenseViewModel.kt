package com.travelassistant.travella.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.travelassistant.travella.data.database.ExpenseDatabase
import com.travelassistant.travella.data.dao.CategoryTotal
import com.travelassistant.travella.data.model.ExpenseEntity
import com.travelassistant.travella.data.service.CurrencyService
import com.travelassistant.travella.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import com.travelassistant.travella.data.model.SimpleExpense

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ExpenseDatabase.getDatabase(application).expenseDao()
    private val currencyService = CurrencyService()
    private val repository = ExpenseRepository(dao, currencyService)

    val allExpenses: Flow<List<ExpenseEntity>> = repository.allExpenses

    private val _selectedTripId = MutableStateFlow<Int?>(null)
    val selectedTripId: StateFlow<Int?> = _selectedTripId

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _selectedDateRange = MutableStateFlow<Pair<Long, Long>?>(null)
    val selectedDateRange: StateFlow<Pair<Long, Long>?> = _selectedDateRange

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses

    private val _categoryTotals = MutableStateFlow<List<CategoryTotal>>(emptyList())
    val categoryTotals: StateFlow<List<CategoryTotal>> = _categoryTotals

    private val _supportedCurrencies = MutableStateFlow<List<String>>(emptyList())
    val supportedCurrencies: StateFlow<List<String>> = _supportedCurrencies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _displayCurrency = MutableStateFlow("USD") // New: Currency to display amounts in
    val displayCurrency: StateFlow<String> = _displayCurrency

    // Filtered and converted expenses based on current filters and display currency
    val filteredExpenses: Flow<List<ExpenseEntity>> = combine(
        allExpenses,
        _selectedTripId,
        _selectedCategory,
        _selectedDateRange,
        _displayCurrency // Combine with display currency
    ) { expenses, tripId, category, dateRange, displayCurrency ->
        var filtered = expenses

        tripId?.let { id ->
            filtered = filtered.filter { it.tripId == id }
        }

        category?.let { cat ->
            filtered = filtered.filter { it.category == cat }
        }

        dateRange?.let { (start, end) ->
            filtered = filtered.filter { it.date in start..end }
        }

        // Convert amounts to the display currency
        filtered.map { expense ->
            val convertedAmount = repository.convertAmount(expense.amountInUSD, "USD", displayCurrency)
            expense.copy(amountInUSD = convertedAmount) // Update amountInUSD for display
        }
    }.onEach {
        // Recalculate total expenses whenever filtered expenses change
        _totalExpenses.value = it.sumOf { exp -> exp.amountInUSD }
    }.catch { e ->
        _errorMessage.value = "Error filtering or converting expenses: ${e.message}"
        emit(emptyList()) // Emit empty list on error
    }


    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Load supported currencies
                _supportedCurrencies.value = repository.getSupportedCurrencies()

                // Refresh currency rates
                repository.refreshCurrencyRates()

                // Load statistics (initial load, will be updated by filteredExpenses flow)
                // _totalExpenses.value = repository.getTotalExpenses() // This is now handled by the combine flow
                _categoryTotals.value = repository.getExpensesByCategory()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load expense data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addExpense(simpleExpense: SimpleExpense) { // Changed to SimpleExpense
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Convert SimpleExpense to ExpenseEntity, ensuring amount is stored in USD
                val expenseEntity = ExpenseEntity(
                    title = simpleExpense.title,
                    amount = simpleExpense.amount, // Original amount
                    currency = simpleExpense.currency, // Original currency (should be USD now)
                    amountInUSD = simpleExpense.amount, // Since we fixed input to USD, amountInUSD is the same
                    category = simpleExpense.category,
                    date = simpleExpense.date,
                    paymentMethod = simpleExpense.paymentMethod,
                    description = simpleExpense.description,
                    location = simpleExpense.location
                )
                repository.insertExpense(expenseEntity)
                refreshStatistics()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add expense: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                repository.updateExpense(expense)
                refreshStatistics()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update expense: ${e.message}"
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
                refreshStatistics()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete expense: ${e.message}"
            }
        }
    }

    fun setTripFilter(tripId: Int?) {
        _selectedTripId.value = tripId
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun setDateRangeFilter(startDate: Long?, endDate: Long?) {
        _selectedDateRange.value = if (startDate != null && endDate != null) {
            Pair(startDate, endDate)
        } else {
            null
        }
    }

    fun clearFilters() {
        _selectedTripId.value = null
        _selectedCategory.value = null
        _selectedDateRange.value = null
    }

    fun updateDisplayCurrency(currency: String) {
        _displayCurrency.value = currency
    }

    private suspend fun refreshStatistics() {
        try {
            // _totalExpenses.value = repository.getTotalExpenses() // This is now handled by the combine flow
            _categoryTotals.value = repository.getExpensesByCategory()
        } catch (e: Exception) {
            _errorMessage.value = "Failed to refresh statistics: ${e.message}"
        }
    }

    fun getExpensesByTrip(tripId: Int): Flow<List<ExpenseEntity>> {
        return repository.getExpensesByTrip(tripId)
    }

    fun getGeneralExpenses(): Flow<List<ExpenseEntity>> {
        return repository.getGeneralExpenses()
    }

    suspend fun getTotalExpensesByTrip(tripId: Int): Double {
        return try {
            repository.getTotalExpensesByTrip(tripId)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to get trip expenses: ${e.message}"
            0.0
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Get expenses for current month
    fun getCurrentMonthExpenses(): Flow<List<ExpenseEntity>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endOfMonth = calendar.timeInMillis

        return repository.getExpensesByDateRange(startOfMonth, endOfMonth)
    }

    // Get expenses for current week
    fun getCurrentWeekExpenses(): Flow<List<ExpenseEntity>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfWeek = calendar.timeInMillis

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endOfWeek = calendar.timeInMillis

        return repository.getExpensesByDateRange(startOfWeek, endOfWeek)
    }
}
