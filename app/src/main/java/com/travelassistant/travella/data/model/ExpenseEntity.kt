package com.travelassistant.travella.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tripId: Int? = null,
    val title: String,
    val amount: Double,
    val currency: String = "USD",
    val amountInUSD: Double,
    val category: String,
    val date: Long,
    val paymentMethod: String,
    val description: String = "",
    val receiptPath: String? = null,
    val location: String = "",
    val isRecurring: Boolean = false,
    val tags: String = ""
)
