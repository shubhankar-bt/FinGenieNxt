package com.shubhanya.fingenienxt.ui.theme // Your package name

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


// Using the new color definitions from your Color.kt
private val AppDarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryVariant,
    onPrimaryContainer = DarkOnPrimary, // Or a lighter shade if DarkPrimaryVariant is very dark

    secondary = DarkSecondaryPink,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryVariant,
    onSecondaryContainer = DarkOnSecondary,

    tertiary = DarkTertiaryPurple,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = Color(0xFF4A0072), // Darker purple container
    onTertiaryContainer = DarkOnTertiary,

    error = DarkErrorRed,
    onError = DarkOnError,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = DarkOnError,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    outline = DarkOnSurfaceVariant.copy(alpha = 0.5f),
    outlineVariant = DarkSurfaceVariant.copy(alpha = 0.7f) // For less prominent outlines
)

private val AppLightColorScheme = lightColorScheme(
    primary = LightPrimaryBlue,
    onPrimary = LightOnPrimary,
    primaryContainer = Color(0xFFD1E4FF), // Lighter blue container
    onPrimaryContainer = Color(0xFF001B3E),

    secondary = LightSecondaryPink,
    onSecondary = LightOnSecondary,
    secondaryContainer = Color(0xFFFFD8E4),
    onSecondaryContainer = Color(0xFF3D001E),

    tertiary = LightTertiaryPurple,
    onTertiary = LightOnTertiary,
    tertiaryContainer = Color(0xFFF3D8FF),
    onTertiaryContainer = Color(0xFF330044),

    error = LightErrorRed,
    onError = LightOnError,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightSurface,
    onSurface = LightOnSurface,

    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    outline = LightOnSurfaceVariant.copy(alpha = 0.5f),
    outlineVariant = LightSurfaceVariant.copy(alpha = 0.7f)
)

@Composable
fun FinGenieNxtTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Set to false to strictly use your custom theme, which is what we want now.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme // Default to light theme if system isn't dark
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // For dark themes, a dark status bar is common.
            // For light themes, primary or surface color might be used.
            window.statusBarColor = colorScheme.background.toArgb() // Match app background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme // True for light, False for dark

            // Optional: Set navigation bar color (system navigation buttons)
            // window.navigationBarColor = colorScheme.surfaceColorAtElevation(3.dp).toArgb()
            // WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming you have Typography.kt defined
        //shapes = Shapes,         // Assuming you have Shapes.kt defined
        content = content
    )
}
