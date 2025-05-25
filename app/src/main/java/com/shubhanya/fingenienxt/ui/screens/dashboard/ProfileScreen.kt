package com.shubhanya.fingenienxt.ui.screens.dashboard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
// CRITICAL: Ensure this imports your Firestore UserProfile data class
import com.shubhanya.fingenienxt.auth.AuthViewModel
import com.shubhanya.fingenienxt.data.local.entity.UserProfile
import com.shubhanya.fingenienxt.profile.ProfileViewModel
import com.shubhanya.fingenienxt.ui.navigation.Screen
import com.shubhanya.fingenienxt.ui.theme.FinGenieNxtTheme
import java.util.Calendar

// Import your specific theme colors if needed for direct use, e.g.:
// import com.shubhanya.fingenienxt.ui.theme.DarkPrimaryBlue
// import com.shubhanya.fingenienxt.ui.theme.LightPrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (userProfile == null) { // Fetch profile only if not already loaded
            profileViewModel.fetchUserProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()) // Make the whole screen scrollable
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Info Header
            UserProfileHeader(userProfile = userProfile)

            Spacer(Modifier.height(24.dp))

            // Profile Options Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ProfileOptionItem(
                        icon = Icons.Filled.Edit,
                        text = "Edit Profile",
                        onClick = { /* TODO: Navigate to Edit Profile Screen */
                            Toast.makeText(context, "Edit Profile Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileOptionItem(
                        icon = Icons.Filled.Palette, // Example for theme/appearance
                        text = "Appearance",
                        onClick = { /* TODO: Navigate to Appearance/Theme Settings */
                            Toast.makeText(context, "Appearance Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileOptionItem(
                        icon = Icons.Filled.Settings,
                        text = "App Settings",
                        onClick = { /* TODO: Navigate to App Settings Screen */
                            Toast.makeText(context, "App Settings Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Help & Support Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ProfileOptionItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        text = "Help & Support",
                        onClick = { /* TODO: Navigate to Help Screen or show dialog */
                            Toast.makeText(context, "Help & Support Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileOptionItem(
                        icon = Icons.Filled.Policy, // Or Shield for Privacy
                        text = "Privacy Policy",
                        onClick = { /* TODO: Open Privacy Policy URL */
                            Toast.makeText(context, "Privacy Policy Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileOptionItem(
                        icon = Icons.Filled.Info,
                        text = "About FinGenie",
                        onClick = { /* TODO: Show About App Dialog/Screen */
                            Toast.makeText(context, "About App Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }


            Spacer(Modifier.weight(1f)) // Pushes logout button to the bottom

            // Logout Button
            Button(
                onClick = {
                    authViewModel.signOut()
//                    navController.navigate(Screen.LoginPhone.route) {
////                        popUpTo(Screen.MainApp.route) {
////                            inclusive = true
////                        }
//
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            inclusive = true
//                        }
//                        launchSingleTop = true
                        Log.d(
                            "ProfileScreen",
                            "After signOut, currentUser is: ${authViewModel.getCurrentUser()?.uid}"
                        )
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Logout", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun UserProfileHeader(userProfile: UserProfile?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for an avatar image or initials
            val initials = userProfile?.name
                ?.split(" ")
                ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                ?.take(2)
                ?.joinToString("") ?: "U"

            Icon(
                imageVector = Icons.Filled.AccountCircle, // Default icon
                contentDescription = "User Avatar",
                modifier = Modifier.size(80.dp), // Slightly smaller than box for padding effect
                tint = MaterialTheme.colorScheme.onPrimaryContainer // Or a specific color
            )
            // If you have an image URL:
            // AsyncImage(model = userProfile?.avatarUrl, contentDescription = "User Avatar", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            // If no image, show initials:
            // Text(initials, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(Modifier.height(12.dp))
        if (userProfile != null) {
            Text(
                text = userProfile.name.ifEmpty { "FinGenie User" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = userProfile.phoneNumber.ifEmpty { "No phone number" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (userProfile.dob.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Cake, contentDescription = "Date of Birth", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "DOB: ${userProfile.dob}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            Text("Loading profile...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp), // Increased vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary // Use primary color for icons
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to $text",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen Light")
@Composable
fun ProfileScreenLightPreview() {
    FinGenieNxtTheme (darkTheme = false) {
        val navController = rememberNavController()
        // For preview, you might need to provide mock ViewModels
        // or ensure Hilt works in previews.
        ProfileScreen(navController = navController)
    }
}

@Preview(showBackground = true, name = "Profile Screen Dark")
@Composable
fun ProfileScreenDarkPreview() {
    FinGenieNxtTheme(darkTheme = true) {
        val navController = rememberNavController()
        ProfileScreen(navController = navController)
    }
}

// Helper extension for Calendar (if not already global)
private fun Calendar.clearTime() { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
private fun Calendar.setTimeToEndOfDay() { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999) }

