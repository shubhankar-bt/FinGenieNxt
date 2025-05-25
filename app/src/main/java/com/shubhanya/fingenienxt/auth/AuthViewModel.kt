package com.shubhanya.fingenienxt.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val authResult: StateFlow<AuthResultState> = repository.authResult
    val userProfileExists: StateFlow<Boolean?> = repository.userProfileExists // For direct observation if needed

    fun sendOtp(phoneNumber: String, activity: Activity) {
        if (phoneNumber.length > 8 && phoneNumber.startsWith("+")) { // Basic validation
            repository.sendOtp(phoneNumber, activity) // This updates authResult internally
        } else {
            // Update authResult directly for UI feedback
            viewModelScope.launch { // This is fine as it's updating a flow for the UI
                repository._authResult.value = AuthResultState.Error("Invalid phone number format. Use +countrycodeXXXXXXXXXX.")
            }
        }
    }

    fun verifyOtp(otp: String) {
        if (otp.length == 6) { // Assuming OTP is 6 digits
            // This will trigger VerificationCompleted state in authResult
            repository.verifyOtpAndPrepareCredential(otp)
        } else {
            viewModelScope.launch {
                repository._authResult.value = AuthResultState.Error("Invalid OTP format. Must be 6 digits.")
            }
        }
    }

    // Called when AuthResultState.VerificationCompleted is observed
    fun processSignInWithCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            repository._authResult.value = AuthResultState.Loading // Set loading before suspend call
            repository.signInWithCredential(credential) // Call the SUSPEND function
        }
    }

    fun checkIfUserIsLoggedIn() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                repository._authResult.value = AuthResultState.Loading // Indicate loading
                repository.checkIfUserProfileExistsAndUpdateAuthState(user.uid) // Call SUSPEND function
            } else {
                repository.signOut() // This resets states to Idle
            }
        }
    }

    fun signOut() {
        repository.signOut() // This updates authResult internally
        Log.d("AuthViewModel", "signOut called, authResult should now be Idle: ${authResult.value}")

    }

    fun resetAuthStateToIdle() {
        repository.resetAuthStateToIdle() // This updates authResult internally
    }

    fun getCurrentUser(): FirebaseUser? { // Add this method
        return repository.getCurrentUser()
    }
}

// Sealed class AuthResultState (ensure this is defined, possibly in the same file or a shared location)
// Make sure it includes:
// object Idle : AuthResultState()
// object Loading : AuthResultState()
// object CodeSent : AuthResultState()
// data class VerificationCompleted(val credential: PhoneAuthCredential) : AuthResultState()
// object Success : AuthResultState() // Logged in, profile exists
// object NewUser : AuthResultState() // Logged in, new user
// data class Error(val message: String) : AuthResultState()
