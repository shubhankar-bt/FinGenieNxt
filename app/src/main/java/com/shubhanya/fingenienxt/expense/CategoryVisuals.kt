package com.shubhanya.fingenienxt.expense

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet // Bills
import androidx.compose.material.icons.filled.CardGiftcard // Gifts/Other
import androidx.compose.material.icons.filled.Fastfood // Food
import androidx.compose.material.icons.filled.FitnessCenter // Health/Fitness
import androidx.compose.material.icons.filled.LocalGroceryStore // Groceries
import androidx.compose.material.icons.filled.Movie // Entertainment
import androidx.compose.material.icons.filled.QuestionMark // Default/Other
import androidx.compose.material.icons.filled.ShoppingCart // Shopping
import androidx.compose.material.icons.filled.Train // Transport
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// You can expand this map with more categories and specific icons
object CategoryVisuals {
    private val categoryMap = mapOf(
        "Food" to Icons.Filled.Fastfood,
        "Transport" to Icons.Filled.Train,
        "Groceries" to Icons.Filled.LocalGroceryStore,
        "Bills" to Icons.Filled.AccountBalanceWallet,
        "Entertainment" to Icons.Filled.Movie,
        "Health" to Icons.Filled.FitnessCenter,
        "Shopping" to Icons.Filled.ShoppingCart,
        "Other" to Icons.Filled.CardGiftcard
        // Add more categories as needed
    )

    fun getIcon(category: String): ImageVector {
        return categoryMap[category] ?: Icons.Filled.QuestionMark // Default icon
    }

    // Optional: You can define distinct colors per category too if desired,
    // but for now, we'll rely on icons and the main theme.
    // fun getColor(category: String): Color {
    //     return when(category) {
    //         "Food" -> Color.Red
    //         else -> LimePrimary // Default to a theme color
    //     }
    // }
}
