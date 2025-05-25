package com.shubhanya.fingenienxt.ui.theme // Your package name

import androidx.compose.ui.graphics.Color

// New Palette - Inspired by the uploaded image (Dark Theme Focus)
val DarkPrimaryBlue = Color(0xFF4A90E2) // A prominent blue from the image
val DarkPrimaryVariant = Color(0xFF357ABD) // A slightly darker shade for containers
val DarkSecondaryPink = Color(0xFFE91E63) // A vibrant pink/magenta accent
val DarkSecondaryVariant = Color(0xFFD81B60)
val DarkTertiaryPurple = Color(0xFF9C27B0) // A purple accent

val DarkBackground = Color(0xFF121212) // Common dark theme background (very dark gray/off-black)
// val DarkBackground = Color(0xFF1A1A2E) // Alternative: A very dark navy/purple from some image examples
val DarkSurface = Color(0xFF1E1E1E) // Slightly lighter than background for cards, dialogs
val DarkSurfaceVariant = Color(0xFF2C2C2E) // For less prominent surfaces or card outlines

val DarkOnPrimary = Color(0xFFFFFFFF) // White text on primary blue
val DarkOnSecondary = Color(0xFFFFFFFF) // White text on pink
val DarkOnTertiary = Color(0xFFFFFFFF) // White text on purple
val DarkOnBackground = Color(0xFFE1E3E1) // Light gray/white text on dark background
val DarkOnSurface = Color(0xFFE1E3E1) // Light gray/white text on dark surface
val DarkOnSurfaceVariant = Color(0xFFB0B3B8) // Slightly dimmer text for surface variants

val DarkErrorRed = Color(0xFFCF6679) // Standard Material dark error
val DarkOnError = Color(0xFF000000) // Black text on error

// Corresponding Light Theme Palette (Derived)
val LightPrimaryBlue = Color(0xFF1976D2) // A good standard blue
val LightPrimaryVariant = Color(0xFF63A4FF)
val LightSecondaryPink = Color(0xFFD81B60)
val LightTertiaryPurple = Color(0xFF8E24AA)

val LightBackground = Color(0xFFF4F6F8) // Very light gray, almost white
val LightSurface = Color(0xFFFFFFFF)   // White for cards, dialogs
val LightSurfaceVariant = Color(0xFFE8EAF6)

val LightOnPrimary = Color(0xFFFFFFFF)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1C1B1F) // Dark text on light background
val LightOnSurface = Color(0xFF1C1B1F)
val LightOnSurfaceVariant = Color(0xFF444746)

val LightErrorRed = Color(0xFFB00020)
val LightOnError = Color(0xFFFFFFFF)

// Chart Specific Accent Colors (can be part of the main palette or separate)
// These will be used for pie chart slices and potentially other chart elements.
// Inspired by the vibrant accents in the sample image.
val ChartAccentBlue = Color(0xFF00A9FF)
val ChartAccentPink = Color(0xFFFF4081)
val ChartAccentPurple = Color(0xFFAB47BC)
val ChartAccentGreen = Color(0xFF00C853) // A bright green accent
val ChartAccentYellow = Color(0xFFFFD600)
val ChartAccentOrange = Color(0xFFFF6D00)
val ChartAccentTeal = Color(0xFF00BFA5)

val DashboardGradientStartBlueLight = Color(0xFF65B0FF) // Lighter blue for light theme
val DashboardGradientEndBlueLight = Color(0xFF0069C0)   // Deeper blue for light theme
val DashboardGradientStartBlueDark = Color(0xFF2C79C5) // Adjusted for dark theme
val DashboardGradientEndBlueDark = Color(0xFF00366C)