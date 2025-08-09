package com.travelassistant.travella.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val label: String,
    val icon: ImageVector,
    val color: Color
) {
    Food("Food & Dining", Icons.Default.Restaurant, Color(0xFFE57373)),
    Travel("Transportation", Icons.Default.DirectionsCar, Color(0xFF64B5F6)),
    Hotel("Accommodation", Icons.Default.Hotel, Color(0xFF9575CD)),
    Shopping("Shopping", Icons.Default.ShoppingCart, Color(0xFFFFB74D)),
    Activities("Entertainment", Icons.Default.LocalActivity, Color(0xFF81C784)),
    Flights("Flights", Icons.Default.Flight, Color(0xFF4DD0E1)),
    Drinks("Drinks", Icons.Default.LocalBar, Color(0xFFBA68C8)),
    Fuel("Fuel", Icons.Default.LocalGasStation, Color(0xFFFF8A65)),
    Medical("Medical", Icons.Default.LocalHospital, Color(0xFFFF7043)),
    Groceries("Groceries", Icons.Default.ShoppingBasket, Color(0xFF66BB6A)),
    Bills("Bills & Utilities", Icons.Default.Receipt, Color(0xFF78909C)),
    Education("Education", Icons.Default.School, Color(0xFF42A5F5)),
    Insurance("Insurance", Icons.Default.Security, Color(0xFF8D6E63)),
    Gifts("Gifts", Icons.Default.CardGiftcard, Color(0xFFEC407A)),
    Health("Health & Fitness", Icons.Default.FitnessCenter, Color(0xFF26A69A)),
    Others("Others", Icons.Default.MoreHoriz, Color.Gray);

    companion object {
        fun fromLabel(label: String): ExpenseCategory {
            return values().firstOrNull { it.label == label } ?: Others
        }

        fun getTravelCategories(): List<ExpenseCategory> {
            return listOf(Flights, Hotel, Travel, Food, Activities, Shopping, Drinks)
        }

        fun getDailyCategories(): List<ExpenseCategory> {
            return listOf(Food, Groceries, Bills, Fuel, Medical, Shopping, Others)
        }
    }
}