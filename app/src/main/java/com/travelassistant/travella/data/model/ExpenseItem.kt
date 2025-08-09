// model/ExpenseItem.kt
package com.travelassistant.travella.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "expenses")
data class ExpenseItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tripId: Int? = null,
    val date: Long,
    val category: String,
    val itemName: String,
    val amount: Double,
    val currency: String = "USD",
    val method: String = "Cash",
    val notes: String = "",
    val receiptUri: String? = null
)