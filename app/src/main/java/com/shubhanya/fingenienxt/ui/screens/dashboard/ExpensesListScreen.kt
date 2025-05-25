package com.shubhanya.fingenienxt.ui.screens.dashboard // Or your preferred package

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort // For sorting
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList // For filter options
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shubhanya.fingenienxt.data.local.entity.Expense
// CRITICAL: Ensure this imports your Firestore Expense data class
import com.shubhanya.fingenienxt.expense.ExpenseResult
import com.shubhanya.fingenienxt.expense.ExpenseTimeFilter
import com.shubhanya.fingenienxt.expense.ExpenseViewModel
import com.shubhanya.fingenienxt.ui.screens.transactions.ExpenseItem
// Import the ExpenseListItem from the Canvas (ensure this path is correct)
// Import your theme's blue colors for the gradient card
import com.shubhanya.fingenienxt.ui.theme.DarkPrimaryBlue
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientEndBlueDark
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientEndBlueLight
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientStartBlueDark
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientStartBlueLight
import com.shubhanya.fingenienxt.ui.theme.FinGenieNxtTheme
import com.shubhanya.fingenienxt.ui.theme.LightPrimaryBlue


import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Assuming DashboardPeriod enum is defined (as in DashboardScreen.kt)
// enum class DashboardPeriod(val displayName: String) { THIS_MONTH, LAST_MONTH, ALL_TIME }

// Gradient colors for the summary card (can be moved to Color.kt)
val TransactionsGradientStartBlueLight = LightPrimaryBlue.copy(alpha = 0.85f)
val TransactionsGradientEndBlueLight = DarkPrimaryBlue.copy(alpha = 0.95f)
val TransactionsGradientStartBlueDark = DarkPrimaryBlue.copy(alpha = 0.85f)
val TransactionsGradientEndBlueDark = Color(0xFF0A2A58) // A deeper blue for dark theme gradient


// Helper to get a normalized Calendar instance (time set to 00:00:00) for a given Date
private fun Date.toNormalizedCalendar(): Calendar {
    return Calendar.getInstance().apply {
        time = this@toNormalizedCalendar
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
}

// Helper to group expenses by date string (e.g., "yyyy-MM-dd")
private fun groupExpensesByDateString(expenses: List<Expense>): Map<String, List<Expense>> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return expenses.groupBy { formatter.format(it.date) }
}

// Helper to format date string ("yyyy-MM-dd") for display in headers
// Matches the "05 Tuesday, February 2023" style from sample image
private fun formatDateHeaderForListScreen(dateString: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate: Date = parser.parse(dateString) ?: return dateString

    val dayNumberFormatter = SimpleDateFormat("dd", Locale.getDefault())
    val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
    val monthYearFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    return "${dayNumberFormatter.format(parsedDate)} ${dayOfWeekFormatter.format(parsedDate)}, ${monthYearFormatter.format(parsedDate)}"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExpensesListScreen(
    navController: NavHostController,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (expenseId: String) -> Unit
) {
    val context = LocalContext.current
    val expensesFromVM by expenseViewModel.filteredExpenses.collectAsState()
    val totalExpensesValue by expenseViewModel.totalFilteredExpenses.collectAsState()
    val searchQuery by expenseViewModel.searchQuery.collectAsState()
    val currentTimeFilter by expenseViewModel.timeFilter.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    val deleteState by expenseViewModel.deleteExpenseResult.collectAsState()

    var periodDropdownExpanded by remember { mutableStateOf(false) }
    // This local state will mirror the ViewModel's filter for the TopAppBar display.
    val currentDisplayPeriodLabel = remember(currentTimeFilter) {
        when (currentTimeFilter) {
            ExpenseTimeFilter.ALL_TIME -> "All Time"
            ExpenseTimeFilter.CURRENT_MONTH -> "This Month"
            // Add cases for other filters if you expand ExpenseTimeFilter
        }
    }

    val groupedExpenses = remember(expensesFromVM) { groupExpensesByDateString(expensesFromVM) }
    val sortedDateKeys = remember(groupedExpenses) { groupedExpenses.keys.sortedDescending() }

    val amountFormatter = remember { DecimalFormat("₹#,##0.00") }
    val animatedTotalExpenses = remember { Animatable(0f) }

    LaunchedEffect(totalExpensesValue) {
        animatedTotalExpenses.animateTo(
            targetValue = totalExpensesValue.toFloat(),
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(deleteState) { /* ... (same as before) ... */ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearchBar) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { expenseViewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Search transactions...", style = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))) },
                            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                            singleLine = true,
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent, errorContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (searchQuery.isNotEmpty()) expenseViewModel.onSearchQueryChanged("")
                                    else showSearchBar = false
                                }) { Icon(Icons.Default.Clear, "Clear/Close search", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                        )
                    } else {
                        Text("Transactions", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    if (showSearchBar) {
                        IconButton(onClick = { showSearchBar = false; expenseViewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Close Search", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    } else if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
                actions = {
                    if (!showSearchBar) {
                        // Period Selector moved to actions
                        Box {
                            TextButton(onClick = { periodDropdownExpanded = true }) {
                                Text(currentDisplayPeriodLabel, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                Icon(
                                    imageVector = if (periodDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Select Period",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            DropdownMenu(
                                expanded = periodDropdownExpanded,
                                onDismissRequest = { periodDropdownExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                            ) {
                                DropdownMenuItem(text = { Text("All Time") }, onClick = { expenseViewModel.onTimeFilterChanged(ExpenseTimeFilter.ALL_TIME); periodDropdownExpanded = false })
                                DropdownMenuItem(text = { Text("This Month") }, onClick = { expenseViewModel.onTimeFilterChanged(ExpenseTimeFilter.CURRENT_MONTH); periodDropdownExpanded = false })
                            }
                        }
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(Icons.Default.Search, "Search Transactions", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp) // Flatter look
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = DarkPrimaryBlue, // Using a blue from your theme
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Filled.Add, "Add New Expense")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background) // e.g., DarkBackground
        ) {
            // Top Summary Card
            AnimatedVisibility(visible = expensesFromVM.isNotEmpty() && !showSearchBar) {
                TransactionListSummaryCard(
                    periodLabel = currentDisplayPeriodLabel,
                    totalAmount = animatedTotalExpenses.value.toDouble(),
                    formatter = amountFormatter
                )
            }

            if (expensesFromVM.isEmpty() && searchQuery.isBlank()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No transactions for this period. Tap '+' to add one!", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            } else if (expensesFromVM.isEmpty() && searchQuery.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No results for '${searchQuery}'. Try a different search.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp )
                ) {
                    sortedDateKeys.forEach { dateKeyString ->
                        val expensesOnThisDate = groupedExpenses[dateKeyString] ?: emptyList()
                        if (expensesOnThisDate.isNotEmpty()){
                            stickyHeader {
                                TransactionListDateHeader(
                                    dateString = dateKeyString,
                                    dailyTotal = expensesOnThisDate.sumOf { it.amount },
                                    formatter = amountFormatter
                                )
                            }
                            items(items = expensesOnThisDate, key = { expense -> "expense_list_item_${expense.id}" }) { expense ->
                                ExpenseItem( // Using the imported item composable
                                    expense = expense,
                                    formatter = amountFormatter,
                                    onItemClick = { onNavigateToEditExpense(expense.id) },
                                    onDeleteConfirm = { expenseViewModel.deleteExpense(expense.id) },
                                    modifier = Modifier.animateItemPlacement(tween(300))
                                )
                                if (expensesOnThisDate.last() != expense) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 72.dp)) // Indent divider from icon
                                }
                            }
                            item { Spacer(Modifier.height(16.dp)) } // More space after each date group
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListSummaryCard(periodLabel: String, totalAmount: Double, formatter: DecimalFormat) {
    val isDark = isSystemInDarkTheme()
    // Using the blue gradient colors defined at the top of DashboardScreen.kt
    val gradientStart = if (isDark) DashboardGradientStartBlueDark else DashboardGradientStartBlueLight
    val gradientEnd = if (isDark) DashboardGradientEndBlueDark else DashboardGradientEndBlueLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp), // Padding around the card
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(gradientStart, gradientEnd)))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = periodLabel, // e.g., "February 2023" or "This Month"
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "-${formatter.format(totalAmount)}", // Total expenses for the period
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White // Or a specific expense color if preferred
            )
        }
    }
}

@Composable
fun TransactionListDateHeader(dateString: String, dailyTotal: Double, formatter: DecimalFormat) { // Renamed
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background) // Use main background for less visual clutter
            .padding(vertical = 12.dp, horizontal = 4.dp), // Added a bit more padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDateHeaderForListScreen(dateString), // Using the new formatter
            style = MaterialTheme.typography.bodySmall, // Made it slightly smaller as per sample
            fontWeight = FontWeight.SemiBold, // Bolder date
            color = MaterialTheme.colorScheme.onSurfaceVariant // Subtler color
        )
        Text(
            text = "-${formatter.format(dailyTotal)}",
            style = MaterialTheme.typography.bodyMedium, // Consistent with item amount
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true, name = "Expenses List Dark Theme")
@Composable
fun ExpensesListScreenDarkPreview() {
    FinGenieNxtTheme (darkTheme = true) { // Assuming FinGenieTheme handles your new dark theme
        ExpensesListScreen(
            navController = rememberNavController(),
            onNavigateToAddExpense = {},
            onNavigateToEditExpense = {}
        )
    }
}

// Helper extension for Calendar (if not already global)
private fun Calendar.clearTime() { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
private fun Calendar.setTimeToEndOfDay() { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999) }

