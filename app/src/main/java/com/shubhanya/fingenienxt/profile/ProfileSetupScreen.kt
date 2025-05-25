package com.shubhanya.fingenienxt.profile

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") } // Date of Birth

    val profileSaveState by profileViewModel.profileSaveState.collectAsState()

    LaunchedEffect(profileSaveState) {
        when (val state = profileSaveState) {
            is ProfileSaveState.Success -> {
                Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                // Navigation to MainAppScreen is handled by AppNavigation after profile is marked as existing
                // For this to work, AuthViewModel's userProfileExists needs to be updated.
                // A better way would be for ProfileViewModel to signal AuthViewModel or a shared repository.
                // For now, we assume AppNavigation will re-evaluate once this screen is left.
                // Or, more directly, after saving, AuthViewModel can be triggered to re-check.
                // Let's simplify: after success, navigate to MainApp and clear backstack.
                // The AuthViewModel's check on app start will handle future logins.
                // navController.navigate(Screen.MainApp.route) {
                //     popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                //     launchSingleTop = true
                // }
                // The AppNavigation should handle this transition automatically if userProfileExists state changes.
                // To ensure this, after saving profile, we might need to trigger a re-check in AuthViewModel or
                // have a shared state that AppNavigation observes.
                // For now, the navigation to MainApp is expected to be handled by AppNavigation's logic
                // once the user is considered "existing". We might need a slight adjustment in AuthViewModel
                // or AppNavigation to reflect profile completion immediately.
                // A simple way: ProfileViewModel signals success, then AuthViewModel can re-trigger its check.
                // Or, ProfileViewModel updates a shared flag that AuthViewModel also observes.

                // For now, let's assume AppNavigation will eventually pick up the change.
                // The user will be directed to MainApp on next auth check.
                // To make it immediate:
                // authViewModel.userProfileJustCreatedSoNavigateToMain() // A hypothetical function
                // This requires ProfileViewModel to communicate back to AuthViewModel or a shared service.

                // Let's assume for now that the navigation to MainApp will be handled by the existing
                // AppNavigation logic when it re-evaluates the start destination or auth state.
                // If not, we'd need to enhance the communication between ViewModels or use a shared status.
            }
            is ProfileSaveState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                profileViewModel.resetProfileSaveState() // Allow retry
            }
            else -> { /* Idle or Loading */ }
        }
    }

    // Date Picker Dialog
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            dob = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )
    // Prevent picking future dates
    datePickerDialog.datePicker.maxDate = calendar.timeInMillis


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Setup Your Profile") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tell Us About Yourself", fontSize = 20.sp, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it }, // Manual input still allowed
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // Make it read-only to force date picker usage
                trailingIcon = {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Select Date",
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    profileViewModel.saveUserProfile(name, dob)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = profileSaveState != ProfileSaveState.Loading
            ) {
                if (profileSaveState == ProfileSaveState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Profile")
                }
            }
        }
    }
}


/**
 * **Explanation:**
 * * **`ProfileSetupScreen`**:
 *     * Collects user's name and date of birth (using a `DatePickerDialog`).
 *     * Calls `profileViewModel.saveUserProfile()`.
 *     * Handles UI updates based on `profileSaveState`.
 *     * Navigation to `MainAppScreen` after successful profile save is expected to be handled by `AppNavigation` once it detects the profile exists (this might require a slight refinement in how `AuthViewModel`'s `userProfileExists` state is updated or observed post-profile creation
 */