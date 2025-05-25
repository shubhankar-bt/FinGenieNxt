package com.shubhanya.fingenienxt.ui.navigation


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shubhanya.fingenienxt.auth.AuthResultState
import com.shubhanya.fingenienxt.auth.AuthViewModel
import com.shubhanya.fingenienxt.auth.OtpScreen
import com.shubhanya.fingenienxt.auth.PhoneNumberScreen
import com.shubhanya.fingenienxt.profile.ProfileSetupScreen
import com.shubhanya.fingenienxt.profile.ProfileViewModel
import com.shubhanya.fingenienxt.ui.screens.dashboard.MainScreen



// --- App Navigation Host ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authResult.collectAsState()

    // Check initial login state only once
    LaunchedEffect(key1 = Unit) {
        Log.d("AppNavigation", "Initial auth check triggered.")
        authViewModel.checkIfUserIsLoggedIn()
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            // Simple Splash Screen - shows a loading indicator while auth state is resolved
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (authState == AuthResultState.Loading || authState == AuthResultState.Idle && authViewModel.getCurrentUser() == null) {
                    CircularProgressIndicator()
                } else if (authState == AuthResultState.Idle && authViewModel.getCurrentUser() != null) {
                    // This case might happen if checkIfUserIsLoggedIn is still running or just finished
                    // And hasn't yet transitioned to Success/NewUser. A loader is fine.
                    CircularProgressIndicator()
                }
                // Text("Loading FinGenie...") // Optional text
            }
        }
        composable(Screen.LoginPhone.route) {
            PhoneNumberScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.LoginOtp.route) {
            OtpScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.ProfileSetup.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            ProfileSetupScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
        composable(Screen.MainApp.route) {
            MainScreen(appGlobalNavController = navController)
        }
    }

    // Observer for auth state changes to trigger navigation
    // This LaunchedEffect handles navigation based on the authState.
    // Line 58 from your error message is likely within this 'when' block.
    LaunchedEffect(authState, navController.currentDestination) { // Add currentDestination to re-evaluate if needed
        val currentRoute = navController.currentDestination?.route
        Log.d("AppNavigation", "Auth State Changed: $authState, Current Route: $currentRoute")

        when (val currentAuthState = authState) {
            is AuthResultState.Success -> {
                if (currentRoute == Screen.ProfileSetup.route) {
                    // Specific handling for navigating from ProfileSetup to MainApp
                    Log.d("AppNavigation", "Navigating to MainApp (Success) from ProfileSetupScreen")
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.ProfileSetup.route) { inclusive = true } // Pop ProfileSetup itself
                        launchSingleTop = true
                    }
                } else if (currentRoute != Screen.MainApp.route) {
                    // General case for navigating to MainApp (e.g., returning user after OTP)
                    Log.d("AppNavigation", "Navigating to MainApp (Success) from $currentRoute")
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true } // Clear the whole auth flow
                        launchSingleTop = true
                    }
                }
            }
            is AuthResultState.NewUser -> {
                if (currentRoute != Screen.ProfileSetup.route) {
                    Log.d("AppNavigation", "Navigating to ProfileSetup (NewUser)")
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthResultState.CodeSent -> {
                if (currentRoute != Screen.LoginOtp.route) {
                    Log.d("AppNavigation", "Navigating to LoginOtp (CodeSent)")
                    navController.navigate(Screen.LoginOtp.route) {
                        // Optionally popUpTo LoginPhone if you want to clear it from backstack
                        // popUpTo(Screen.LoginPhone.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthResultState.VerificationCompleted -> {
                // ViewModel processes the credential, which will then update authState
                // to Success, NewUser, or Error.
                Log.d("AppNavigation", "Processing VerificationCompleted state.")
                authViewModel.processSignInWithCredential(currentAuthState.credential)
            }
            is AuthResultState.Error -> {
                // Error state is typically handled by showing a Toast in the specific screen (PhoneNumberScreen, OtpScreen).
                // Navigation to login might be needed if error occurs on a protected screen,
                // but usually errors during login keep the user on the login/OTP screen.
                // If an error occurs and the user is on Splash, navigate to Login.
                if (currentRoute == Screen.Splash.route || currentRoute == Screen.MainApp.route || currentRoute == Screen.ProfileSetup.route) {
                    Log.d("AppNavigation", "Error state, navigating to LoginPhone from $currentRoute")
                    navController.navigate(Screen.LoginPhone.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            AuthResultState.Idle -> {
                // If Idle and not on a login path already, and not on splash (unless splash is stuck)
                // This means user logged out or was never logged in.
                if (currentRoute != Screen.LoginPhone.route &&
                    currentRoute != Screen.LoginOtp.route &&
                    currentRoute != Screen.Splash.route) { // Avoid navigating from splash if it's the initial Idle state before check
                    Log.d("AppNavigation", "Idle state, navigating to LoginPhone from $currentRoute")
                    navController.navigate(Screen.LoginPhone.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                } else if (currentRoute == Screen.Splash.route && authViewModel.getCurrentUser() == null) {
                    // If stuck on splash and no user, go to login.
                    // This handles the case where initial check completes and finds no user.
                    Log.d("AppNavigation", "Idle on Splash, no user, navigating to LoginPhone")
                    navController.navigate(Screen.LoginPhone.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            AuthResultState.Loading -> {
                Log.d("AppNavigation", "Auth State is Loading. No navigation change.")
                // Usually, loading indicators are shown on the current screen.
                // No explicit navigation change needed here unless it's a global loading screen.
            }
            // No 'else' needed if all sealed class subtypes are handled.
        }
    }
}

/**
 * **Explanation:**
 * * **`Screen` Sealed Class**: Defines all possible navigation destinations in your app with unique routes.
 * * **`AppNavigation` Composable**:
 *     * Sets up the `NavHostController` and `NavHost`.
 *     * `hiltViewModel()` is used to get instances of ViewModels scoped to the navigation graph.
 *     * `LaunchedEffect(key1 = Unit)`: Checks the initial login state when `AppNavigation` is first composed.
 *     * `NavHost`: Defines the navigation graph. Each `composable` block corresponds to a screen.
 *     * `LaunchedEffect(authState, userProfileExists)`: Observes changes in authentication state and `userProfileExists` status to trigger navigation automatically (e.g., after successful login, navigate to main app or profile setup).
 *     * `determineStartDestination()`: Logic to decide the initial screen based on whether the user is already logged in and if their profile is set up.
 * * Navigation logic:
 *     * `navController.navigate("route")`: Navigates to a screen.
 *     * `popUpTo(...)`: Clears the back stack up to a certain destination.
 *     * `launchSingleTop = true`: Avoids multiple copies of the same screen on top of each oth
 */