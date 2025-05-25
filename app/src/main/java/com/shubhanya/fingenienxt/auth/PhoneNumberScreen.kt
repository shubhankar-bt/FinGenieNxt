package com.shubhanya.fingenienxt.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("+91") } // Default to India, or make it dynamic
    val authState by authViewModel.authResult.collectAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthResultState.CodeSent -> {
                // Navigation to OTP screen is handled in AppNavigation
                Toast.makeText(context, "OTP Sent!", Toast.LENGTH_SHORT).show()
            }
            is AuthResultState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthStateToIdle() // Reset to allow retry
            }
            else -> { /* Do nothing for other states here */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("FinGenie - Login") })
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
            Text("Enter Your Phone Number", fontSize = 20.sp, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number (e.g., +91XXXXXXXXXX)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (phoneNumber.isNotBlank()) {
                        authViewModel.sendOtp(phoneNumber, context as Activity)
                    } else {
                        Toast.makeText(context, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState != AuthResultState.Loading
            ) {
                if (authState == AuthResultState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Send OTP")
                }
            }
        }
    }
}