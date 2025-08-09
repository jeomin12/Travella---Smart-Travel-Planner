package com.travelassistant.travella.data.model


data class SimpleExpense(
    val title: String,
    val amount: Double,
    val currency: String = "USD",
    val category: String,
    val date: Long,
    val paymentMethod: String,
    val description: String = "",
    val location: String = ""
)
