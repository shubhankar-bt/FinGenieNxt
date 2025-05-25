package com.shubhanya.fingenienxt.ui.screens.dashboard


// --- JetChart Imports ---
// --- End JetChart Imports ---

// MPAndroidChart Imports
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.shubhanya.fingenienxt.data.local.entity.Expense
import com.shubhanya.fingenienxt.expense.CategoryVisuals
import com.shubhanya.fingenienxt.expense.ExpenseViewModel
import com.shubhanya.fingenienxt.profile.ProfileViewModel
import com.shubhanya.fingenienxt.ui.theme.ChartAccentBlue
import com.shubhanya.fingenienxt.ui.theme.ChartAccentGreen
import com.shubhanya.fingenienxt.ui.theme.ChartAccentOrange
import com.shubhanya.fingenienxt.ui.theme.ChartAccentPink
import com.shubhanya.fingenienxt.ui.theme.ChartAccentPurple
import com.shubhanya.fingenienxt.ui.theme.ChartAccentTeal
import com.shubhanya.fingenienxt.ui.theme.ChartAccentYellow
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientEndBlueDark
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientEndBlueLight
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientStartBlueDark
import com.shubhanya.fingenienxt.ui.theme.DashboardGradientStartBlueLight
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.github.mikephil.charting.components.Legend as MPChartLegend

enum class DashboardPeriod(val displayName: String) {
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    ALL_TIME("All Time")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToAddExpense: () -> Unit = { navController.navigate("add_expense") },
    onNavigateToViewAllExpenses: () -> Unit = { navController.navigate("expenses_list") }
) {
    val allExpensesFromVM by expenseViewModel.allExpenses.collectAsState()
    var selectedPeriod by remember { mutableStateOf(DashboardPeriod.THIS_MONTH) }
    var periodDropdownExpanded by remember { mutableStateOf(false) }

    val userProfile by profileViewModel.userProfile.collectAsState()
    LaunchedEffect(key1 = Unit) {
        if (userProfile == null) {
            profileViewModel.fetchUserProfile()
        }
    }
    val userName = remember(userProfile) {
        userProfile?.name?.split(" ")?.firstOrNull()?.takeIf { it.isNotBlank() } ?: "User"
    }

    val amountFormatter = remember { DecimalFormat("₹#,##0.00") }
    val shortDateFormatter = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }
    val fullDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val filteredExpensesForPeriod = remember(allExpensesFromVM, selectedPeriod) {
        val nowCalendar = Calendar.getInstance()
        when (selectedPeriod) {
            DashboardPeriod.THIS_MONTH -> {
                val startOfMonth = Calendar.getInstance()
                    .apply { time = nowCalendar.time; set(Calendar.DAY_OF_MONTH, 1); clearTime() }
                val endOfMonth = Calendar.getInstance().apply {
                    time = nowCalendar.time; set(
                    Calendar.DAY_OF_MONTH,
                    getActualMaximum(Calendar.DAY_OF_MONTH)
                ); setTimeToEndOfDay()
                }
                allExpensesFromVM.filter {
                    !it.date.before(startOfMonth.time) && !it.date.after(
                        endOfMonth.time
                    )
                }
            }

            DashboardPeriod.LAST_MONTH -> {
                val startOfLastMonth = Calendar.getInstance().apply {
                    time = nowCalendar.time; add(
                    Calendar.MONTH,
                    -1
                ); set(Calendar.DAY_OF_MONTH, 1); clearTime()
                }
                val endOfLastMonth = Calendar.getInstance().apply {
                    time = nowCalendar.time; add(
                    Calendar.MONTH,
                    -1
                ); set(
                    Calendar.DAY_OF_MONTH,
                    getActualMaximum(Calendar.DAY_OF_MONTH)
                ); setTimeToEndOfDay()
                }
                allExpensesFromVM.filter {
                    !it.date.before(startOfLastMonth.time) && !it.date.after(
                        endOfLastMonth.time
                    )
                }
            }

            DashboardPeriod.ALL_TIME -> allExpensesFromVM
        }
    }

    val totalSpendingForPeriod = remember(filteredExpensesForPeriod) {
        filteredExpensesForPeriod.sumOf { it.amount }
    }
    val animatedTotalSpending = remember { Animatable(totalSpendingForPeriod.toFloat()) }
    LaunchedEffect(totalSpendingForPeriod) {
        animatedTotalSpending.animateTo(
            targetValue = totalSpendingForPeriod.toFloat(),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    val categorySpendingForPeriod = remember(filteredExpensesForPeriod) {
        filteredExpensesForPeriod
            .groupBy { it.category.ifEmpty { "Uncategorized" } }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
            .entries.sortedByDescending { it.value }
    }

    val spendingTrendData = remember(filteredExpensesForPeriod) {
        filteredExpensesForPeriod
            .sortedBy { it.date }
            .groupBy { fullDateFormatter.format(it.date) }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
            .entries.sortedBy { it.key }
            .takeLast(7)
    }

    val averageSpendingPerTransaction =
        remember(filteredExpensesForPeriod, totalSpendingForPeriod) {
            if (filteredExpensesForPeriod.isNotEmpty()) totalSpendingForPeriod / filteredExpensesForPeriod.size else 0.0
        }
    val topSpendingCategory = remember(categorySpendingForPeriod) {
        categorySpendingForPeriod.firstOrNull()
    }

    val currentColorScheme = MaterialTheme.colorScheme
    // --- UPDATED PIE CHART COLORS to be more diverse ---
    val pieChartColorsForMP = remember {
        (ColorTemplate.JOYFUL_COLORS.toList() +
                ColorTemplate.VORDIPLOM_COLORS.toList() +
                ColorTemplate.PASTEL_COLORS.toList() +
                ColorTemplate.MATERIAL_COLORS.toList() + // Add more templates for variety
                listOf( // Add some theme-based colors as well if needed
                    ChartAccentBlue.toArgb(),
                    ChartAccentGreen.toArgb(),
                    currentColorScheme.primary.toArgb(),
                    currentColorScheme.secondary.toArgb()
                )).distinct() // Ensure no duplicate color integers if templates overlap
    }
    val barChartMainColorForMP = remember { ChartAccentBlue.toArgb() }


    Scaffold(
        topBar = {
            EnhancedTopAppBar(
                userName = userName,
                selectedPeriodDisplayName = selectedPeriod.displayName,
                onPeriodSelectorClick = { periodDropdownExpanded = !periodDropdownExpanded },
                isPeriodDropdownExpanded = periodDropdownExpanded,
                onDismissPeriodDropdown = { periodDropdownExpanded = false },
                onPeriodSelected = { period ->
                    selectedPeriod = period
                    periodDropdownExpanded = false
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "Add Expense")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp), // Increased spacing
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                GradientBlueSummaryCard(
                    title = "Spending (${selectedPeriod.displayName})",
                    animatedAmount = animatedTotalSpending.value.toDouble(),
                    transactionCount = filteredExpensesForPeriod.size,
                    formatter = amountFormatter,
                    onViewReportClick = {
                        // TODO: Navigate to a detailed report screen or show a dialog
                        // For now, can just log or show a Toast
                        // Toast.makeText(navController.context, "View Report Clicked", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                QuickStatsCard(
                    transactionCount = filteredExpensesForPeriod.size,
                    averageSpending = averageSpendingPerTransaction,
                    topCategory = topSpendingCategory?.key,
                    topCategoryAmount = topSpendingCategory?.value,
                    formatter = amountFormatter
                )
            }

            item {
                DashboardSectionCard(title = "Category Breakdown", icon = Icons.Filled.DonutLarge) {
                    if (categorySpendingForPeriod.isNotEmpty()) {
                        ActualPieChartMP(
                            categorySpending = categorySpendingForPeriod,
                            pieChartColors = pieChartColorsForMP
                        )
                    } else {
                        NoDataMessage("No spending in ${selectedPeriod.displayName} for category breakdown.")
                    }
                }
            }

            item {
                DashboardSectionCard(title = "Spending Trend", icon = Icons.Filled.TrendingUp) {
                    if (spendingTrendData.isNotEmpty()) {
                        ActualBarChartMP(
                            trendData = spendingTrendData,
                            barColor = barChartMainColorForMP,
                            shortDateFormatter = shortDateFormatter
                        )
                    } else {
                        NoDataMessage("Not enough data in ${selectedPeriod.displayName} for spending trend.")
                    }
                }
            }

            if (filteredExpensesForPeriod.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 4.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Recent Transactions (${selectedPeriod.displayName})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (filteredExpensesForPeriod.size > 3) {
                            TextButton(onClick = onNavigateToViewAllExpenses) {
                                Text("View All", color = MaterialTheme.colorScheme.primary)
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    "View All",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                items(filteredExpensesForPeriod.take(3), key = { it.id }) { expense ->
                    CompactDashboardExpenseItem(
                        expense = expense,
                        formatter = amountFormatter,
                        shortDateFormatter = shortDateFormatter
                    ) {
                        navController.navigate("edit_expense/${expense.id}")
                    }
                }
            } else if (selectedPeriod == DashboardPeriod.ALL_TIME && allExpensesFromVM.isEmpty()) {
                item {
                    NoDataMessage(
                        "No transactions recorded yet. Tap '+' to add your first one!",
                        icon = Icons.Filled.ListAlt
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTopAppBar(
    userName: String,
    selectedPeriodDisplayName: String,
    onPeriodSelectorClick: () -> Unit,
    isPeriodDropdownExpanded: Boolean,
    onDismissPeriodDropdown: () -> Unit,
    onPeriodSelected: (DashboardPeriod) -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Hi, $userName!",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            Box(modifier = Modifier.padding(end = 8.dp)) {
                TextButton(onClick = onPeriodSelectorClick, shape = MaterialTheme.shapes.medium) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = "Select Period",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        selectedPeriodDisplayName,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Icon(
                        imageVector = if (isPeriodDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Toggle period dropdown",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded = isPeriodDropdownExpanded,
                    onDismissRequest = onDismissPeriodDropdown,
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    )
                ) {
                    DashboardPeriod.values().forEach { period ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    period.displayName,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = { onPeriodSelected(period) }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    )
}

@Composable
fun GradientBlueSummaryCard(
    title: String,
    animatedAmount: Double,
    transactionCount: Int,
    formatter: DecimalFormat,
    onViewReportClick: () -> Unit // Callback for the new button/icon
) {
    val isDark = isSystemInDarkTheme()
    val gradientStart =
        if (isDark) DashboardGradientStartBlueDark else DashboardGradientStartBlueLight
    val gradientEnd = if (isDark) DashboardGradientEndBlueDark else DashboardGradientEndBlueLight

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp, hoveredElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient( // Apply gradient to the whole Row or the left part
                        colors = listOf(gradientStart, gradientEnd)
                    )
                )
        ) {
            // Left Part (with gradient)
            Column(
                modifier = Modifier
                    .weight(0.7f) // Takes ~70% of the width
                    .padding(start = 20.dp, top = 20.dp, bottom = 20.dp, end = 12.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    formatter.format(animatedAmount),
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 28.sp), // Adjusted size
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.ListAlt,
                        contentDescription = "Transactions",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "$transactionCount transactions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Right Part (solid background, content centered)
            Box(
                modifier = Modifier
                    .weight(0.3f) // Takes ~30% of the width
                    .fillMaxHeight() // Match height of the gradient part
                    .background(gradientEnd.copy(alpha = 0.7f)) // Slightly darker solid blue, or another complementary color
                    .clickable(onClick = onViewReportClick)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.QueryStats, // Changed to a "report" like icon
                        contentDescription = "View Report",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Details",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatsCard(
    transactionCount: Int,
    averageSpending: Double,
    topCategory: String?,
    topCategoryAmount: Double?,
    formatter: DecimalFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large, // Using large shape for consistency
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Quick Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) // Softer divider
            Spacer(Modifier.height(12.dp))

            QuickStatItem(
                icon = Icons.Filled.Payments,
                label = "Transactions:",
                value = "$transactionCount"
            )
            Spacer(Modifier.height(8.dp))
            QuickStatItem(
                icon = Icons.Filled.Functions,
                label = "Avg. / Transaction:",
                value = formatter.format(averageSpending)
            )
            topCategory?.let { category ->
                topCategoryAmount?.let { amount ->
                    Spacer(Modifier.height(8.dp))
                    QuickStatItem(
                        icon = CategoryVisuals.getIcon(category),
                        label = "Top Category ($category):",
                        value = formatter.format(amount)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Added vertical padding
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp) // Slightly larger icon
        )
        Spacer(Modifier.width(12.dp)) // More space
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f) // Ensure label takes available space
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge, // Make value slightly more prominent
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


//*********************************

// --- MPAndroidChart Pie Chart ---
@Composable
fun ActualPieChartMP(
    categorySpending: List<Map.Entry<String, Double>>,
    pieChartColors: List<Int> // Expecting List<Int> (ARGB colors)
) {
    val context = LocalContext.current
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val chartLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

    if (categorySpending.isEmpty()) {
        NoDataMessage("No data for category breakdown.")
        return
    }

    AndroidView(
        factory = { ctx ->
            PieChart(ctx).apply {
                // Basic Setup
                this.description.isEnabled = false
                this.isDrawHoleEnabled = true
                this.setHoleColor(Color.Transparent.toArgb()) // Transparent hole
                this.holeRadius = 55f
                this.transparentCircleRadius = 60f
                this.setTransparentCircleColor(surfaceColor)
                this.setTransparentCircleAlpha(110)


                // Entry Labels (Category Names on Slices) - often better to disable if using legend
                this.setDrawEntryLabels(false)
                // this.setEntryLabelColor(chartLabelColor)
                // this.setEntryLabelTextSize(10f)

                // Center Text (Optional)
                // this.setDrawCenterText(true)
                // this.centerText = "Categories"
                // this.setCenterTextColor(onSurfaceColor)
                // this.setCenterTextSize(14f)

                // Legend
                this.legend.isEnabled = true
                this.legend.verticalAlignment = MPChartLegend.LegendVerticalAlignment.CENTER
                this.legend.horizontalAlignment = MPChartLegend.LegendHorizontalAlignment.RIGHT
                this.legend.orientation = MPChartLegend.LegendOrientation.VERTICAL
                this.legend.setDrawInside(false)
                this.legend.textColor = chartLabelColor
                this.legend.textSize = 10f
                this.legend.form = MPChartLegend.LegendForm.CIRCLE
                this.legend.formSize = 8f
                this.legend.xEntrySpace = 5f
                this.legend.yEntrySpace = 3f

                // Animation & Interaction
                this.animateY(1200)
                this.setUsePercentValues(true) // To display percentages on slices
            }
        },
        update = { pieChart ->
            val entries = ArrayList<PieEntry>()
            for (entry in categorySpending) {
                // Value and Label for each slice
                entries.add(PieEntry(entry.value.toFloat(), entry.key))
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.sliceSpace = 2f
                this.colors = pieChartColors // Use our themed ARGB colors
                this.valueTextColor = onSurfaceColor // Color for percentage values on slices
                this.valueTextSize = 11f
                this.valueTypeface = android.graphics.Typeface.DEFAULT_BOLD
                this.valueFormatter = PercentFormatter(pieChart) // Show percentage
                this.setDrawValues(true) // Show values (percentages) on slices
                this.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE // Percentages outside
                this.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                this.valueLinePart1OffsetPercentage = 80f
                this.valueLinePart1Length = 0.4f
                this.valueLinePart2Length = 0.5f
                this.valueLineColor = chartLabelColor
            }

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.invalidate() // Refresh chart
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Adjust height as needed
    )
}

// --- MPAndroidChart Bar Chart ---
@Composable
fun ActualBarChartMP(
    trendData: List<Map.Entry<String, Double>>, // Key: "yyyy-MM-dd", Value: amount
    barColor: Int, // ARGB color
    shortDateFormatter: SimpleDateFormat
) {
    val context = LocalContext.current
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val axisLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val gridLineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f).toArgb()

    if (trendData.isEmpty()) {
        NoDataMessage("No data for spending trend.")
        return
    }

    // Create a list of date labels for the X-axis formatter
    val dateLabels = remember(trendData) {
        trendData.map {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.key)?.let { date ->
                    shortDateFormatter.format(date)
                } ?: it.key // Fallback to original string if parsing fails
            } catch (e: Exception) {
                it.key
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            BarChart(ctx).apply {
                // Basic Setup
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(true) // Show value above bar

                // X Axis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = axisLabelColor
                xAxis.textSize = 9f
                xAxis.granularity = 1f // Minimum interval between axis values
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return dateLabels.getOrNull(value.toInt()) ?: value.toInt().toString()
                    }
                }
                xAxis.setLabelRotationAngle(-45f) // Rotate labels if they overlap

                // Left Y Axis
                axisLeft.textColor = axisLabelColor
                axisLeft.textSize = 10f
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = gridLineColor
                axisLeft.axisMinimum = 0f // Start Y axis at 0
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return DecimalFormat("₹#,##0").format(value.toDouble())
                    }
                }

                // Right Y Axis
                axisRight.isEnabled = false // Disable right Y axis

                // Legend
                legend.isEnabled = false // Disable legend for single dataset bar chart

                // Animation
                animateY(1000)
            }
        },
        update = { barChart ->
            val entries = ArrayList<BarEntry>()
            trendData.forEachIndexed { index, entry ->
                // X value is the index, Y value is the amount
                entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            }

            val dataSet = BarDataSet(entries, "Spending").apply {
                this.color = barColor // Use the passed ARGB color
                this.valueTextColor = onSurfaceColor
                this.valueTextSize = 9f
                this.setDrawValues(true) // Show values on top of bars
                this.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return DecimalFormat("#,##0").format(value.toDouble()) // No currency symbol on bars for cleaner look
                    }
                }
            }

            val barData = BarData(dataSet)
            barData.barWidth = 0.6f // Adjust bar width

            barChart.data = barData
            barChart.setFitBars(true) // Make the bars fit into the viewport (if few bars)
            barChart.invalidate() // Refresh
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}


@Composable
fun DashboardSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large, // Consistent large rounding
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp) // Ensure enough space for chart content
                    .clip(MaterialTheme.shapes.medium) // Inner content clip
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(), // Content will fill the Box
                    horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
                    verticalArrangement = Arrangement.Center // Center content vertically
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun NoDataMessage(message: String, icon: ImageVector? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon?.let {
            Icon(
                it,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(12.dp))
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun CompactDashboardExpenseItem(
    expense: Expense,
    formatter: DecimalFormat,
    shortDateFormatter: SimpleDateFormat, // Use the passed formatter
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium, // Consistent medium rounding
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = CategoryVisuals.getIcon(expense.category),
                contentDescription = expense.category,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        CircleShape
                    )
                    .padding(6.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    expense.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface // Main text color
                )
                Text(
                    // Use the passed shortDateFormatter
                    "${expense.category} • ${shortDateFormatter.format(expense.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "-${formatter.format(expense.amount)}", // Add minus for expenses
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error, // Expenses are "negative"
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


// Helper for capitalizing period names - can be moved to a utility file
private fun String.capitalize(locale: Locale): String {
    if (this.isNotEmpty()) {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }
    return this
}

// Helper for Calendar minute, second, millisecond setting for clarity
private var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) = set(Calendar.MINUTE, value)

private var Calendar.second: Int
    get() = get(Calendar.SECOND)
    set(value) = set(Calendar.SECOND, value)

private var Calendar.millisecond: Int
    get() = get(Calendar.MILLISECOND)
    set(value) = set(Calendar.MILLISECOND, value)


private fun Calendar.clearTime() {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(
        Calendar.SECOND,
        0
    ); set(Calendar.MILLISECOND, 0)
}

private fun Calendar.setTimeToEndOfDay() {
    set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(
        Calendar.SECOND,
        59
    ); set(Calendar.MILLISECOND, 999)
}

