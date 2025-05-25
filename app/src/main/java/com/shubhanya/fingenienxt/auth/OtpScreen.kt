package com.shubhanya.fingenienxt.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shubhanya.fingenienxt.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var otp by remember { mutableStateOf("") }
    val authState by authViewModel.authResult.collectAsState()

    // Navigation based on authState is handled by AppNavigation's LaunchedEffect

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthResultState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                // Optionally navigate back or allow retry by resetting state
                authViewModel.resetAuthStateToIdle() // Reset to allow retry, might navigate back via AppNavigation
            }
            is AuthResultState.Success, is AuthResultState.NewUser -> {
                // Navigation is handled in AppNavigation
            }
            else -> { /* Do nothing for other states here */ }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verify OTP") })
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
            Text("Enter OTP", fontSize = 20.sp, style = MaterialTheme.typography.headlineSmall)
            Text("An OTP has been sent to your phone.", fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                label = { Text("6-Digit OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation() // Optional: hides OTP
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (otp.length == 6) {
                        authViewModel.verifyOtp(otp)
                    } else {
                        Toast.makeText(context, "OTP must be 6 digits", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState != AuthResultState.Loading
            ) {
                if (authState == AuthResultState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Verify OTP")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = {
                // Allow user to go back to phone number entry if OTP fails or they want to change number
                // This assumes AppNavigation will handle navigating back to LoginPhone if authState is reset
                authViewModel.resetAuthStateToIdle() // This will trigger navigation in AppNavigation
                navController.navigate(Screen.LoginPhone.route) {
                    popUpTo(Screen.LoginOtp.route) { inclusive = true }
                }
            }) {
                Text("Change Phone Number?")
            }
        }
    }
}

/**
 * **Explanation:**
 * * **`PhoneNumberScreen`**:
 *     * Takes user's phone number.
 *     * Calls `authViewModel.sendOtp()`.
 *     * Shows loading indicator and handles errors via `Toast`.
 *     * Navigation to OTP screen upon `CodeSent` is now primarily handled by the `AppNavigation`'s `LaunchedEffect`.
 * * **`OtpScreen`**:
 *     * Takes the 6-digit OTP.
 *     * Calls `authViewModel.verifyOtp()`.
 *     * Navigation to `ProfileSetupScreen` or `MainAppScreen` upon success/new user is handled by `AppNavigation`.
 * * `LaunchedEffect(authState)` in each screen is used for showing `Toast` messages for errors specific to that screen's actions. The primary navigation logic resides in `AppNavigatio
 */