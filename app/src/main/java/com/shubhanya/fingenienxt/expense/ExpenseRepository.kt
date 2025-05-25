package com.shubhanya.fingenienxt.expense

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
import com.shubhanya.fingenienxt.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

sealed class ExpenseResult {
    object Loading : ExpenseResult()
    data class Success<T>(val data: T) : ExpenseResult()
    data class Error(val message: String) : ExpenseResult()
    object Idle : ExpenseResult()
}

class ExpenseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val TAG = "ExpenseRepository"
    private val expensesCollection = firestore.collection("expenses")

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun addExpense(expense: Expense): ExpenseResult { /* ... as before ... */
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.w(TAG, "addExpense: User not logged in.")
            return ExpenseResult.Error("User not logged in")
        }
        val expenseWithUser = expense.copy(userId = userId)
        return try {
            expensesCollection.add(expenseWithUser).await()
            Log.d(TAG, "Expense added successfully for user: $userId, desc: ${expense.description}")
            ExpenseResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding expense for user: $userId", e)
            ExpenseResult.Error("Failed to add expense: ${e.message}")
        }
    }

    suspend fun updateExpense(expense: Expense): ExpenseResult { /* ... as before ... */
        val userId = getCurrentUserId()
        if (userId == null || expense.userId != userId) {
            Log.w(TAG, "updateExpense: User not logged in or unauthorized. Op User: $userId, Expense User: ${expense.userId}")
            return ExpenseResult.Error("User not logged in or unauthorized")
        }
        if (expense.id.isBlank()) {
            Log.w(TAG, "updateExpense: Expense ID is missing for update.")
            return ExpenseResult.Error("Expense ID is missing for update")
        }
        return try {
            expensesCollection.document(expense.id).set(expense).await()
            Log.d(TAG, "Expense updated successfully: ${expense.id}")
            ExpenseResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating expense: ${expense.id}", e)
            ExpenseResult.Error("Failed to update expense: ${e.message}")
        }
    }

    suspend fun deleteExpense(expenseId: String): ExpenseResult {
        val userId = getCurrentUserId() // You might want to verify ownership before delete in rules too
        if (userId == null) {
            return ExpenseResult.Error("User not logged in")
        }
        return try {
            expensesCollection.document(expenseId).delete().await()
            Log.d(TAG, "Expense deleted successfully: $expenseId")
            ExpenseResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting expense", e)
            ExpenseResult.Error("Failed to delete expense: ${e.message}")
        }
    }

    // NEW METHOD to get a single expense
    suspend fun getExpenseById(expenseId: String): ExpenseResult {
        val userId = getCurrentUserId()
        if (userId == null) {
            return ExpenseResult.Error("User not logged in")
        }
        return try {
            val documentSnapshot = expensesCollection.document(expenseId).get().await()
            val expense = documentSnapshot.toObject<Expense>()
            if (expense != null && expense.userId == userId) { // Verify ownership
                Log.d(TAG, "Expense fetched successfully: ${expense.id}")
                ExpenseResult.Success(expense)
            } else if (expense != null && expense.userId != userId) {
                Log.w(TAG, "User $userId tried to fetch expense ${expense.id} owned by ${expense.userId}")
                ExpenseResult.Error("Expense not found or unauthorized.")
            }
            else {
                Log.w(TAG, "Expense not found with ID: $expenseId")
                ExpenseResult.Error("Expense not found.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching expense by ID: $expenseId", e)
            ExpenseResult.Error("Failed to fetch expense: ${e.message}")
        }
    }


    fun getExpensesFlow(): Flow<List<Expense>> { /* ... as before with debugging ... */
        val userId = getCurrentUserId()
        Log.i(TAG, "getExpensesFlow: Initializing for userId: $userId")
        if (userId == null) {
            Log.w(TAG, "getExpensesFlow: userId is null, returning empty flow immediately.")
            return MutableStateFlow(emptyList())
        }
        return expensesCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .snapshots() // REAL-TIME LISTENER
            .map { snapshot ->
                Log.d(TAG, "getExpensesFlow (userId: $userId): Snapshot received. HasPendingWrites: ${snapshot.metadata.hasPendingWrites()}, Size: ${snapshot.size()}, isEmpty: ${snapshot.isEmpty}")
                val expenses = snapshot.toObjects<Expense>() // KTX extension
                Log.i(TAG, "getExpensesFlow (userId: $userId): Mapped to ${expenses.size} Expense objects.")
                if (expenses.isNotEmpty()) {
                    Log.d(TAG, "getExpensesFlow: First expense example - ID: ${expenses.first().id}, Desc: ${expenses.first().description}, Date: ${expenses.first().date}")
                }
                expenses
            }
            .catch { exception ->
                Log.e(TAG, "getExpensesFlow: !!! ERROR IN SNAPSHOT LISTENER (userId: $userId) !!!", exception)
                emit(emptyList()) // Emit an empty list on error to prevent app crash
            }
            .onStart { Log.d(TAG, "getExpensesFlow (userId: $userId): Flow collection started.") }
            .onCompletion { cause -> Log.d(TAG, "getExpensesFlow (userId: $userId): Flow collection completed. Cause: $cause") }
    }
    fun getCurrentMonthExpensesFlow(): Flow<List<Expense>> { /* ... as before with debugging ... */
        val userId = getCurrentUserId()
        Log.i(TAG, "getCurrentMonthExpensesFlow: Initializing for userId: $userId")
        if (userId == null) {
            Log.w(TAG, "getCurrentMonthExpensesFlow: userId is null, returning empty flow immediately.")
            return MutableStateFlow(emptyList())
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
        val firstDayOfMonth = calendar.time

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23); calendar.set(Calendar.MINUTE, 59); calendar.set(Calendar.SECOND, 59); calendar.set(Calendar.MILLISECOND, 999)
        val lastDayOfMonth = calendar.time
        Log.d(TAG, "getCurrentMonthExpensesFlow: Date range for query: $firstDayOfMonth TO $lastDayOfMonth")


        return expensesCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", firstDayOfMonth)
            .whereLessThanOrEqualTo("date", lastDayOfMonth)
            .orderBy("date", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                Log.d(TAG, "getCurrentMonthExpensesFlow (userId: $userId): Snapshot received. HasPendingWrites: ${snapshot.metadata.hasPendingWrites()}, Size: ${snapshot.size()}")
                val expenses = snapshot.toObjects<Expense>()
                Log.i(TAG, "getCurrentMonthExpensesFlow (userId: $userId): Mapped to ${expenses.size} Expense objects for current month.")
                expenses
            }
            .catch { exception ->
                Log.e(TAG, "getCurrentMonthExpensesFlow: !!! ERROR IN SNAPSHOT LISTENER (userId: $userId) !!!", exception)
                emit(emptyList())
            }
            .onStart { Log.d(TAG, "getCurrentMonthExpensesFlow (userId: $userId): Flow collection started.") }
            .onCompletion { cause -> Log.d(TAG, "getCurrentMonthExpensesFlow (userId: $userId): Flow collection completed. Cause: $cause") }
    }
}
