package com.shubhanya.fingenienxt.ui.navigation

// --- Navigation Routes ---
sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen") // Optional: if you want a splash screen
    object LoginPhone : Screen("login_phone")
    object LoginOtp : Screen("login_otp")
    object ProfileSetup : Screen("profile_setup")
    object MainApp : Screen("main_app") // For the main screen with bottom nav
}
