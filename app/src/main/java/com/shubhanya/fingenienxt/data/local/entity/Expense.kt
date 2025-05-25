package com.shubhanya.fingenienxt.data.local.entity

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a single expense item.
 *
 * @property id The unique identifier for the expense (usually the Firestore document ID).
 * @property description A brief description of the expense.
 * @property amount The monetary value of the expense.
 * @property category The category of the expense (e.g., Food, Transport, Bills).
 * @property date The date the expense occurred.
 * @property createdAt Timestamp of when the expense was created in Firestore.
 */
data class Expense(
    @DocumentId
    val id: String = "",
    val userId: String = "", // To associate expense with a user
    val description: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Date = Date(),
    @ServerTimestamp
    val createdAt: Date? = null
) {
    constructor() : this("", "", "", 0.0, "", Date(), null)
}

// We can later add an enum for categories if we want predefined options:
// enum class ExpenseCategory {
//     FOOD, TRANSPORTATION, HOUSING, UTILITIES, ENTERTAINMENT, HEALTHCARE, OTHER
// }
