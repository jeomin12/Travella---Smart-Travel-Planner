package com.travelassistant.travella.data.dao

import androidx.room.*
import com.travelassistant.travella.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY date DESC")
    fun getExpensesByTrip(tripId: Int): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE tripId IS NULL ORDER BY date DESC")
    fun getGeneralExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amountInUSD) FROM expenses")
    suspend fun getTotalExpenses(): Double?

    @Query("SELECT SUM(amountInUSD) FROM expenses WHERE tripId = :tripId")
    suspend fun getTotalExpensesByTrip(tripId: Int): Double?

    @Query("SELECT category, SUM(amountInUSD) as total FROM expenses GROUP BY category ORDER BY total DESC")
    suspend fun getExpensesByCategory(): List<CategoryTotal>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): ExpenseEntity?

    @Query("DELETE FROM expenses WHERE tripId = :tripId")
    suspend fun deleteExpensesByTrip(tripId: Int)
}

data class CategoryTotal(
    val category: String,
    val total: Double
)