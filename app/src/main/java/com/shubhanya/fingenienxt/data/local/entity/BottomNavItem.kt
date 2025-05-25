package com.shubhanya.fingenienxt.data.local.entity

import androidx.compose.ui.graphics.vector.ImageVector

// --- Data class for Bottom Navigation Items ---
data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)