package com.shubhanya.fingenienxt.ui.screens.dashboard // Or your specific package for this screen

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
// CORRECT IMPORT for your Firestore Expense data class
import com.shubhanya.fingenienxt.expense.ExpenseResult
import com.shubhanya.fingenienxt.expense.ExpenseViewModel
import com.shubhanya.fingenienxt.expense.SelectedExpenseState // Import the new state
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    expenseIdForEdit: String? = null
) {
    val context = LocalContext.current
    val isEditMode = expenseIdForEdit != null

    // Local states for form fields
    var description by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") } // Use a separate state for amount TextField
    var selectedCategory by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) } // Default to today

    val categories = listOf("Food", "Transport", "Groceries", "Bills", "Entertainment", "Health", "Shopping", "Other")
    var categoryExpanded by remember { mutableStateOf(false) }

    // Observe ViewModel states
    val addExpenseOpState by expenseViewModel.addExpenseResult.collectAsState()
    val editExpenseOpState by expenseViewModel.editExpenseResult.collectAsState()
    val loadSelectedExpenseState by expenseViewModel.selectedExpenseState.collectAsState()

    // To ensure fields are populated only once per successful load in edit mode
    var fieldsPopulatedForCurrentEdit by remember(expenseIdForEdit) { mutableStateOf(false) }

    // Load expense for editing
    LaunchedEffect(key1 = expenseIdForEdit) {
        if (isEditMode && expenseIdForEdit != null) {
            fieldsPopulatedForCurrentEdit = false // Reset flag when a new ID comes
            expenseViewModel.loadExpenseForEditing(expenseIdForEdit)
        } else {
            // Reset fields for "add mode" or if expenseIdForEdit becomes null
            description = ""
            amountInput = ""
            selectedCategory = ""
            selectedDate = Date()
            fieldsPopulatedForCurrentEdit = false
            expenseViewModel.clearSelectedExpense() // Reset ViewModel state
        }
    }

    // Populate form fields when an expense is successfully loaded for editing
    LaunchedEffect(key1 = loadSelectedExpenseState) {
        if (isEditMode && loadSelectedExpenseState is SelectedExpenseState.Success && !fieldsPopulatedForCurrentEdit) {
            val expense = (loadSelectedExpenseState as SelectedExpenseState.Success).expense
            description = expense.description
            amountInput = DecimalFormat("0.##").format(expense.amount) // Format for display, avoid trailing .0
            selectedCategory = expense.category
            selectedDate = expense.date
            fieldsPopulatedForCurrentEdit = true // Mark as populated for this edit session
        }
    }

    // Handle results of add/update operations
    LaunchedEffect(key1 = addExpenseOpState) {
        when (val result = addExpenseOpState) {
            is ExpenseResult.Success<*> -> {
                Toast.makeText(context, "Expense added!", Toast.LENGTH_SHORT).show()
                expenseViewModel.resetAddExpenseResult()
                navController.popBackStack()
            }
            is ExpenseResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                expenseViewModel.resetAddExpenseResult()
            }
            else -> Unit
        }
    }
    LaunchedEffect(key1 = editExpenseOpState) {
        when (val result = editExpenseOpState) {
            is ExpenseResult.Success<*> -> {
                Toast.makeText(context, "Expense updated!", Toast.LENGTH_SHORT).show()
                expenseViewModel.resetEditExpenseResult()
                expenseViewModel.clearSelectedExpense() // Important to clear the loaded expense
                navController.popBackStack()
            }
            is ExpenseResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                expenseViewModel.resetEditExpenseResult()
            }
            else -> Unit
        }
    }

    // Date Picker Dialog Logic
    val calendar = remember { Calendar.getInstance() }
    // Update calendar instance when selectedDate (from form state) changes
    LaunchedEffect(selectedDate) {
        calendar.time = selectedDate
    }
    val datePickerDialog = remember(selectedDate) { // Re-create/re-key if selectedDate changes
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis() // Optional: prevent future dates
        }
    }
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Expense" else "Add New Expense") },
                navigationIcon = {
                    IconButton(onClick = {
                        expenseViewModel.clearSelectedExpense() // Clear state before going back
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val amountDouble = amountInput.toDoubleOrNull()
                    if (amountDouble == null || amountDouble <= 0) {
                        Toast.makeText(context, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
                        return@ExtendedFloatingActionButton
                    }
                    if (description.isBlank() || selectedCategory.isBlank()) {
                        Toast.makeText(context, "Description and Category are required.", Toast.LENGTH_SHORT).show()
                        return@ExtendedFloatingActionButton
                    }

                    if (isEditMode && loadSelectedExpenseState is SelectedExpenseState.Success) {
                        val originalExpense = (loadSelectedExpenseState as SelectedExpenseState.Success).expense
                        val updatedExpense = originalExpense.copy(
                            description = description.trim(),
                            amount = amountDouble,
                            category = selectedCategory,
                            date = selectedDate
                        )
                        expenseViewModel.updateExpense(updatedExpense)
                    } else if (!isEditMode) {
                        expenseViewModel.addExpense(description.trim(), amountDouble, selectedCategory, selectedDate)
                    } else if (isEditMode && loadSelectedExpenseState !is SelectedExpenseState.Success) {
                        Toast.makeText(context, "Original expense data not loaded. Cannot update.", Toast.LENGTH_LONG).show()
                    }
                },
                icon = { Icon(if (isEditMode) Icons.Filled.Done else Icons.Filled.Money, contentDescription = "Save") },
                text = { Text(if (isEditMode) "Update Expense" else "Save Expense") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Handle loading/error states for fetching expense in edit mode
            if (isEditMode) {
                when (loadSelectedExpenseState) {
                    is SelectedExpenseState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        return@Scaffold // Prevent form rendering while loading
                    }
                    is SelectedExpenseState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.ErrorOutline, "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Error: ${(loadSelectedExpenseState as SelectedExpenseState.Error).message}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { navController.popBackStack() }) { Text("Go Back") }
                        }
                        return@Scaffold // Prevent form rendering on error
                    }
                    else -> Unit // Success or Idle (Idle shouldn't happen if expenseIdForEdit is not null)
                }
            }

            // Actual Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Consistent padding
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing
            ) {
                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Expense Description") },
                    leadingIcon = { Icon(Icons.Filled.Description, "Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium, // Rounded corners
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                    )
                )

                // Amount Field
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = {
                        val filtered = it.filter { char -> char.isDigit() || char == '.' }
                        if (filtered.count { char -> char == '.' } <= 1) amountInput = filtered
                    },
                    label = { Text("Amount (e.g., 123.45)") },
                    leadingIcon = { Icon(Icons.Filled.Money, "Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                    )
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory.ifEmpty { "Select Category" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        leadingIcon = { Icon(Icons.Filled.Category, "Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) // Themed background
                            .fillMaxWidth(0.9f) // Make dropdown slightly less than full width
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                },
                                modifier = Modifier.clip(MaterialTheme.shapes.small)
                            )
                        }
                    }
                }

                // Date Picker Field (Wrapped for reliable click)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = true, // This Box is what handles the click
                            onClick = {
                                Log.d("AddExpenseScreen", "Date Box wrapper CLICKED! Showing DatePickerDialog.")
                                datePickerDialog.show() // datePickerDialog should be defined in AddExpenseScreen
                            }
                        )
                ) {
                    OutlinedTextField(
                        value = dateFormatter.format(selectedDate), // selectedDate and dateFormatter from AddExpenseScreen
                        onValueChange = {}, // No direct text input
                        readOnly = true,    // Visually indicates it's not for typing
                        label = { Text("Date of Expense") },
                        leadingIcon = { Icon(Icons.Filled.CalendarToday, "Date") },
                        modifier = Modifier.fillMaxWidth(), // TextField fills the Box
                        enabled = false, // IMPORTANT: Makes the TextField itself not intercept pointer events.
                        // The click is handled by the parent Box.
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            // Style the disabled state to look like a normal, non-editable field
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = Color.Transparent, // Ensure background is transparent
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // If you had a trailing icon
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            // You can also set focused/unfocused colors if 'enabled=false' overrides them too much,
                            // but usually styling the disabled state is enough.
                            // Ensure other states like focusedBorderColor are set if needed,
                            // though a disabled field won't typically show focus.
                            focusedBorderColor = MaterialTheme.colorScheme.primary, // Kept for consistency if focus state was ever an issue
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                        )
                    )
                }




                // Show loading indicator for save/update operation
                val currentSaveOrUpdateState = if (isEditMode) editExpenseOpState else addExpenseOpState
                if (currentSaveOrUpdateState is ExpenseResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
            }
        }
    }
}
