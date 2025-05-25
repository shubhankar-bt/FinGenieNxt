package com.shubhanya.fingenienxt.expense

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhanya.fingenienxt.data.local.entity.Expense
// CORRECT IMPORT for your Firestore Expense data class
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

// --- Enums and Sealed Classes for Expense Handling ---

enum class ExpenseTimeFilter {
    ALL_TIME,
    CURRENT_MONTH
    // Add more like LAST_MONTH, CUSTOM_RANGE later
}

sealed class SelectedExpenseState {
    object Idle : SelectedExpenseState()
    object Loading : SelectedExpenseState()
    data class Success(val expense: Expense) : SelectedExpenseState() // Specifically for a loaded Expense
    data class Error(val message: String) : SelectedExpenseState()
}


@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {
    private val TAG = "ExpenseViewModel"

    private val _addExpenseResult = MutableStateFlow<ExpenseResult>(ExpenseResult.Idle)
    val addExpenseResult: StateFlow<ExpenseResult> = _addExpenseResult.asStateFlow()

    private val _editExpenseResult = MutableStateFlow<ExpenseResult>(ExpenseResult.Idle)
    val editExpenseResult: StateFlow<ExpenseResult> = _editExpenseResult.asStateFlow()

    private val _deleteExpenseResult = MutableStateFlow<ExpenseResult>(ExpenseResult.Idle)
    val deleteExpenseResult: StateFlow<ExpenseResult> = _deleteExpenseResult.asStateFlow()

    // State for fetching/holding the expense to be edited, using SelectedExpenseState
    private val _selectedExpenseState = MutableStateFlow<SelectedExpenseState>(SelectedExpenseState.Idle)
    val selectedExpenseState: StateFlow<SelectedExpenseState> = _selectedExpenseState.asStateFlow()

    // Optional derived StateFlow for just the Expense object if AddExpenseScreen still uses it directly
    val selectedExpense: StateFlow<Expense?> = _selectedExpenseState.map { state ->
        (state as? SelectedExpenseState.Success)?.expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _timeFilter = MutableStateFlow(ExpenseTimeFilter.ALL_TIME)
    val timeFilter: StateFlow<ExpenseTimeFilter> = _timeFilter.asStateFlow()

    // Publicly exposed StateFlow for all expenses, primarily for Dashboard
    val allExpenses: StateFlow<List<Expense>> = repository.getExpensesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L, 0L),
            initialValue = emptyList()
        )

    // Specific flow for current month expenses, can also be used by dashboard or other features
    val currentMonthExpenses: StateFlow<List<Expense>> = repository.getCurrentMonthExpensesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L, 0L),
            initialValue = emptyList()
        )


    private val _allExpensesFromRepo: StateFlow<List<Expense>> = repository.getExpensesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L, 0L), emptyList())

    private val _currentMonthExpensesFromRepo: StateFlow<List<Expense>> = repository.getCurrentMonthExpensesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L, 0L), emptyList())

    val filteredExpenses: StateFlow<List<Expense>> = combine(
        _searchQuery,
        _timeFilter,
        _allExpensesFromRepo,
        _currentMonthExpensesFromRepo
    ) { query, filter, allExpenses, monthExpenses ->
        Log.d(TAG, "filteredExpenses: Combining. Query: '$query', Filter: $filter, AllSize: ${allExpenses.size}, MonthSize: ${monthExpenses.size}")
        val expensesToFilter = when (filter) {
            ExpenseTimeFilter.ALL_TIME -> allExpenses
            ExpenseTimeFilter.CURRENT_MONTH -> monthExpenses
        }
        val result = if (query.isBlank()) {
            expensesToFilter
        } else {
            expensesToFilter.filter { expense ->
                expense.description.contains(query, ignoreCase = true) ||
                        expense.category.contains(query, ignoreCase = true)
            }
        }
        Log.d(TAG, "filteredExpenses: Result size: ${result.size}")
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L, 0L), emptyList())

    val totalFilteredExpenses: StateFlow<Double> = filteredExpenses.map { expenses ->
        expenses.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L, 0L), 0.0)


    fun addExpense(description: String, amount: Double, category: String, date: Date) {
        viewModelScope.launch {
            _addExpenseResult.value = ExpenseResult.Loading
            if (description.isBlank() || amount <= 0 || category.isBlank()) {
                _addExpenseResult.value = ExpenseResult.Error("All fields must be filled and amount must be positive.")
                return@launch
            }
            // Ensure the Expense object uses the correct Firestore data class
            val expense = Expense(description = description, amount = amount, category = category, date = date)
            _addExpenseResult.value = repository.addExpense(expense)
        }
    }

    fun updateExpense(expense: Expense) { // Parameter should be your Firestore Expense
        viewModelScope.launch {
            _editExpenseResult.value = ExpenseResult.Loading
            if (expense.description.isBlank() || expense.amount <= 0 || expense.category.isBlank()) {
                _editExpenseResult.value = ExpenseResult.Error("All fields must be filled and amount must be positive for update.")
                return@launch
            }
            _editExpenseResult.value = repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            _deleteExpenseResult.value = ExpenseResult.Loading
            Log.d(TAG, "Attempting to delete expenseId: $expenseId")
            val result = repository.deleteExpense(expenseId)
            _deleteExpenseResult.value = result
            Log.d(TAG, "Delete result for $expenseId: $result")
        }
    }

    fun loadExpenseForEditing(expenseId: String) {
        viewModelScope.launch {
            Log.d(TAG, "loadExpenseForEditing: Attempting to fetch expense with ID: $expenseId")
            _selectedExpenseState.value = SelectedExpenseState.Loading

            when (val resultFromRepository = repository.getExpenseById(expenseId)) {
                is ExpenseResult.Success<*> -> {
                    val expenseData = resultFromRepository.data as? Expense // Cast to your Firestore Expense
                    if (expenseData != null) {
                        _selectedExpenseState.value = SelectedExpenseState.Success(expenseData)
                        Log.i(TAG, "loadExpenseForEditing: Successfully loaded expense: ID=${expenseData.id}, Desc=${expenseData.description}")
                    } else {
                        val errorMsg = "Successfully fetched but data is not a valid Expense object or is null for ID $expenseId."
                        Log.e(TAG, "loadExpenseForEditing: $errorMsg")
                        _selectedExpenseState.value = SelectedExpenseState.Error(errorMsg)
                    }
                }
                is ExpenseResult.Error -> {
                    Log.e(TAG, "loadExpenseForEditing: Error fetching expense for ID $expenseId: ${resultFromRepository.message}")
                    _selectedExpenseState.value = SelectedExpenseState.Error(resultFromRepository.message)
                }
                is ExpenseResult.Loading -> { // This case is less likely if getExpenseById is a simple suspend fun
                    Log.d(TAG, "loadExpenseForEditing: Repository reported loading for ID $expenseId (unexpected for getExpenseById)")
                    // _selectedExpenseState is already Loading
                }
                is ExpenseResult.Idle -> { // Also less likely for a direct fetch
                    val idleMsg = "Repository is Idle after trying to fetch expense for ID $expenseId."
                    Log.w(TAG, "loadExpenseForEditing: $idleMsg")
                    _selectedExpenseState.value = SelectedExpenseState.Error("Expense not found or repository idle.")
                }
            }
        }
    }

    fun clearSelectedExpense() {
        Log.d(TAG, "clearSelectedExpense: Clearing selected expense state to Idle.")
        _selectedExpenseState.value = SelectedExpenseState.Idle
    }

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onTimeFilterChanged(filter: ExpenseTimeFilter) { _timeFilter.value = filter }

    fun resetAddExpenseResult() { _addExpenseResult.value = ExpenseResult.Idle }
    fun resetEditExpenseResult() { _editExpenseResult.value = ExpenseResult.Idle }
    fun resetDeleteExpenseResult() { _deleteExpenseResult.value = ExpenseResult.Idle }
}
