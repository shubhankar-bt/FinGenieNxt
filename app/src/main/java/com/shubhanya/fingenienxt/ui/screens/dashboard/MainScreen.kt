package com.shubhanya.fingenienxt.ui.screens.dashboard
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shubhanya.fingenienxt.auth.AuthViewModel
import com.shubhanya.fingenienxt.data.local.entity.BottomNavItem
import com.shubhanya.fingenienxt.expense.ExpenseViewModel
import com.shubhanya.fingenienxt.profile.ProfileViewModel
import com.shubhanya.fingenienxt.ui.navigation.Screen
import com.shubhanya.fingenienxt.ui.theme.FinGenieNxtTheme


val bottomNavItems = listOf(
    BottomNavItem(
        title = "Dashboard",
        route = "dashboard",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        title = "Expenses",
        route = "expenses_list",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.ListAlt
    ),
    BottomNavItem(
        title = "Add",
        route = "add_expense",
        selectedIcon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircleOutline
    ),
    BottomNavItem(
        title = "Profile",
        route = "profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.PersonOutline
    )
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Add this if Scaffold complains about unused padding
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    appGlobalNavController: NavHostController // This NavController is from AppNavigation
) {
    val mainAppNavController = rememberNavController() // NavController for content within MainScreen
    val authViewModel: AuthViewModel = hiltViewModel() // For logout

    Scaffold(
        bottomBar = {
            NavigationBar { // Material 3 Bottom Navigation Bar
                val navBackStackEntry by mainAppNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            mainAppNavController.navigate(item.route) {
                                popUpTo(mainAppNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        alwaysShowLabel = true // Or false, based on your preference
                    )
                }
            }
        }
    ) { innerPadding -> // innerPadding is provided by Scaffold for content area
        NavHost(
            navController = mainAppNavController,
            startDestination = "dashboard", // Default screen for MainScreen
            modifier = Modifier.padding(innerPadding) // Apply padding
        ) {
            composable("dashboard") {
                val expenseViewModel: ExpenseViewModel = hiltViewModel()
                DashboardScreen(
                    navController = mainAppNavController, // Or bottomBarNavController
                     expenseViewModel = expenseViewModel,
                     onNavigateToAddExpense = { mainAppNavController.navigate("add_expense") },
                     onNavigateToViewAllExpenses = { mainAppNavController.navigate("expenses_list") }
                    // The default implementations in DashboardScreen already do this.
                )
            }

            composable("add_expense") {
                val expenseViewModel: ExpenseViewModel = hiltViewModel()
                AddExpenseScreen(
                    navController = mainAppNavController, // Use the controller for bottom bar tabs
                    expenseViewModel = expenseViewModel,
                    expenseIdForEdit = null
                )
            }

            composable("edit_expense/{expenseId}") { backStackEntry -> // Route for editing
                val expenseId = backStackEntry.arguments?.getString("expenseId")
                val expenseViewModel: ExpenseViewModel = hiltViewModel()
                AddExpenseScreen(
                    navController = mainAppNavController,
                    expenseViewModel = expenseViewModel,
                    expenseIdForEdit = expenseId
                )
            }

            composable("profile") {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    navController = mainAppNavController,
                    profileViewModel = profileViewModel
                )
            }

            composable("expenses_list") {
                val expenseViewModel: ExpenseViewModel = hiltViewModel() // if needed directly, or through callback
                ExpensesListScreen(
                    navController = mainAppNavController, // Or pass specific callbacks
                    expenseViewModel = expenseViewModel,    // Pass the VM
                    onNavigateToAddExpense = { mainAppNavController.navigate("add_expense") },
                    onNavigateToEditExpense = { expenseId ->
                        mainAppNavController.navigate("edit_expense/$expenseId")
                    }
                )
            }
        }
    }
}

// --- Placeholder Screens for Bottom Navigation ---
//@Composable
//fun DashboardScreen(navController: NavHostController) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//        Text("Dashboard Screen", style = MaterialTheme.typography.headlineMedium)
//        // TODO: Implement Dashboard UI (charts, summaries)
//    }
//}


//@Composable
//fun ProfileScreen(
//    navController: NavHostController, // Main app NavController for navigating out on logout
//    authViewModel: AuthViewModel,
//    profileViewModel: ProfileViewModel
//) {
//    val userProfile by profileViewModel.userProfile.collectAsState()
//
//    LaunchedEffect(Unit) {
//        profileViewModel.fetchUserProfile()
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Profile Screen", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        userProfile?.let {
//            Text("Name: ${it.name}", style = MaterialTheme.typography.bodyLarge)
//            Text("Phone: ${it.phoneNumber}", style = MaterialTheme.typography.bodyLarge)
//            Text("DOB: ${it.dob}", style = MaterialTheme.typography.bodyLarge)
//        } ?: Text("Loading profile...", style = MaterialTheme.typography.bodyLarge)
//
//        Spacer(modifier = Modifier.height(32.dp))
//        Button(onClick = {
//            authViewModel.signOut()
//            // Navigation back to login is handled by AppNavigation's LaunchedEffect on authState change
//             navController.navigate(Screen.LoginPhone.route) {
//                popUpTo(Screen.MainApp.route) { inclusive = true }
//             }
//        }) {
//            Text("Logout")
//        }
//    }
//}


//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    FinGenieNxtTheme {
//        MainScreen(rememberNavController())
//    }
//}

//
//```
//**Explanation:**
//* **`BottomNavItem` Data Class**: Represents an item in the bottom navigation bar.
//* **`bottomNavItems` List**: Defines the actual items for your navigation.
//* **`MainScreen` Composable**:
//* Uses `Scaffold` to provide a standard Material Design layout structure.
//* `NavigationBar` (Material 3) is used for the bottom navigation.
//* `NavigationBarItem` creates each tab.
//* It has its own `mainAppNavController` to manage navigation between the screens *within* the `MainScreen` (Dashboard, Expenses, Add, Profile).
//* The `navController` passed into `MainScreen` is the one from `AppNavigation`, used here for global actions like logging out from the Profile screen, which would navigate outside `MainScreen`.
//* `NavHost` within `MainScreen` defines the content area for the selected bottom navigation tab.
//* **Placeholder Screens**: `DashboardScreen`, `ExpensesListScreen`, `AddExpenseScreen`, `ProfileScreen` are simple placeholders for now. We will fill these in.
//* `ProfileScreen` fetches and displays user data and has a Logout button. When logout occurs, `authViewModel.signOut()` is called. The `AppNavigation`'s `LaunchedEffect` observing `authState` will then automatically navigate to the `LoginPhone` scre
