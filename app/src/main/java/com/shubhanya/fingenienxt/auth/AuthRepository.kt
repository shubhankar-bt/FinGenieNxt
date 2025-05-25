package com.shubhanya.fingenienxt.auth

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val TAG = "AuthRepository"

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    // Internal MutableStateFlows - these are updated directly, not via a CoroutineScope here
    internal val _authResult = MutableStateFlow<AuthResultState>(AuthResultState.Idle)
    val authResult: StateFlow<AuthResultState> = _authResult

    internal val _userProfileExists = MutableStateFlow<Boolean?>(null) // Not directly used by AuthResultState anymore
    val userProfileExists: StateFlow<Boolean?> = _userProfileExists // Kept for direct observation if needed elsewhere

    fun sendOtp(phoneNumber: String, activity: Activity) {
        _authResult.value = AuthResultState.Loading
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted (auto-retrieval or instant verification): $credential")
                // Emit state for ViewModel to handle sign-in
                _authResult.value = AuthResultState.VerificationCompleted(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                _authResult.value = AuthResultState.Error("Verification failed: ${e.message ?: "Unknown error"}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                _authResult.value = AuthResultState.CodeSent
            }
        }

        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting phone verification", e)
            _authResult.value = AuthResultState.Error("Error initiating OTP: ${e.message}")
        }
    }

    fun verifyOtpAndPrepareCredential(otp: String) {
        // This function doesn't sign in, just prepares the credential
        // The ViewModel will then call signInWithPreparedCredential
        if (storedVerificationId != null) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
            _authResult.value = AuthResultState.VerificationCompleted(credential)
        } else {
            _authResult.value = AuthResultState.Error("Cannot verify OTP: Verification ID is missing. Try sending OTP again.")
        }
    }

    // SUSPEND FUNCTION: Called from ViewModel's viewModelScope
    suspend fun signInWithCredential(credential: PhoneAuthCredential) {
        // The ViewModel should set Loading state before calling this
        try {
            auth.signInWithCredential(credential).await()
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                Log.d(TAG, "Sign in successful with credential: ${firebaseUser.uid}")
                // After successful sign-in, check profile status
                checkIfUserProfileExistsAndUpdateAuthState(firebaseUser.uid)
            } else {
                _authResult.value = AuthResultState.Error("Sign in failed: User is null after credential sign in.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "signInWithCredential failed", e)
            _authResult.value = AuthResultState.Error("Sign in failed: ${e.message ?: "Unknown error during sign in"}")
        }
    }

    // SUSPEND FUNCTION: Called from ViewModel's viewModelScope
    suspend fun checkIfUserProfileExistsAndUpdateAuthState(uid: String) {
        // This function now directly updates _authResult based on profile existence
        try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                _userProfileExists.value = true // Keep this for direct observation if needed
                _authResult.value = AuthResultState.Success // User logged in, profile exists
            } else {
                _userProfileExists.value = false // Keep this for direct observation
                _authResult.value = AuthResultState.NewUser // User logged in, but new (needs profile setup)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error checking user profile", e)
            _userProfileExists.value = null // Error state for direct observation
            _authResult.value = AuthResultState.Error("Error checking profile: ${e.message ?: "Unknown error"}")
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
        _authResult.value = AuthResultState.Idle
        _userProfileExists.value = null
    }

    fun resetAuthStateToIdle() {
        // Only reset if not in a loading state to avoid interrupting ongoing operations
        if (_authResult.value !is AuthResultState.Loading) {
            _authResult.value = AuthResultState.Idle
        }
    }
}

//```
//**Explanation:**
//* **`AuthRepository`**:
//* Injects `FirebaseAuth` and `FirebaseFirestore`.
//* `sendOtp()`: Initiates the phone number verification process. Needs an `Activity` context for reCAPTCHA.
//* `verifyOtp()`: Verifies the OTP entered by the user.
//* `signInWithPhoneAuthCredential()`: Signs the user in.
//* `checkIfUserProfileExists()`: Checks Firestore if a profile exists for the logged-in user.
//* `_authResult` (MutableStateFlow) and `authResult` (StateFlow): Expose the current state of the authentication process (Idle, Loading, CodeSent, Success, NewUser, Error). UI will observe this.
//* **`AuthViewModel`**:
//* `@HiltViewModel`: Marks it for Hilt injection.
//* Injects `AuthRepository`.
//* Exposes `authResult` and `userProfileExists` from the repository to the UI.
//* Provides functions `sendOtp` and `verifyOtp` that the UI will call. Includes basic validation.
//* `checkIfUserIsLoggedIn()`: To check auth state when the app starts.
//* **`AuthResultState`**: A sealed class to represent the different states of the authentication flow. This makes it easy for the UI to react to chang