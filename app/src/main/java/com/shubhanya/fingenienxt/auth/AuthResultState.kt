package com.shubhanya.fingenienxt.auth

import com.google.firebase.auth.PhoneAuthCredential

/**
 * Represents the different states of the authentication process.
 */
sealed class AuthResultState {
    /** The initial state, no operation has been performed. */
    object Idle : AuthResultState()

    /** An authentication operation is currently in progress. */
    object Loading : AuthResultState()

    /** The OTP code has been successfully sent to the user's phone. */
    object CodeSent : AuthResultState()

    /**
     * Phone number verification is complete (either by OTP or auto-retrieval),
     * and the credential is ready for sign-in.
     * @property credential The PhoneAuthCredential obtained after successful verification.
     */
    data class VerificationCompleted(val credential: PhoneAuthCredential) : AuthResultState()

    /**
     * User has been successfully signed in, and their profile exists.
     * This is the state for a returning user who is fully authenticated.
     */
    object Success : AuthResultState()

    /**
     * User has been successfully signed in, but they are a new user
     * and need to complete their profile setup.
     */
    object NewUser : AuthResultState()

    /**
     * An error occurred during the authentication process.
     * @property message A descriptive message of the error.
     */
    data class Error(val message: String) : AuthResultState()
}


//
//**How this fits:**
//
//1.  **In `AuthRepository.kt`:**
//* When `PhoneAuthProvider.OnVerificationStateChangedCallbacks()`'s `onVerificationCompleted` is called (for auto-retrieval or instant verification), it should set:
//`_authResult.value = AuthResultState.VerificationCompleted(credential)`
//* In `verifyOtpAndPrepareCredential(otp: String)`, after successfully creating the credential, it should set:
//`_authResult.value = AuthResultState.VerificationCompleted(credential)`
//
//2.  **In `AuthViewModel.kt`:**
//* You would have a `LaunchedEffect` or observe the `authResult` StateFlow.
//* When the state becomes `is AuthResultState.VerificationCompleted`, you then call `processSignInWithCredential(state.credential)`:
//
//```kotlin
//// Inside your AuthViewModel or a Composable observing it:
//// (This logic is typically in AppNavigation or the screen observing the ViewModel)
//
//// Example of how AuthViewModel might react (or how AppNavigation reacts to AuthViewModel's state):
//// LaunchedEffect(authResult) {
////     if (authResult is AuthResultState.VerificationCompleted) {
////         authViewModel.processSignInWithCredential((authResult as AuthResultState.VerificationCompleted).credential)
////     }
//// }
//```
//(My previous `AppNavigation` block already includes logic to call `authViewModel.processVerificationCredential(currentAuthState.credential)` when `authState` is `VerificationCompleted`).
//
//3.  **In `ProfileViewModel.kt`:**
//* After a successful profile save for a new user, the call to `authRepository.checkIfUserProfileExistsAndUpdateAuthState(uid)` in the `ProfileViewModel` will lead the `AuthRepository` to eventually set `_authResult.value = AuthResultState.Success`.
//
//By including `VerificationCompleted` in your `AuthResultState` sealed class, the authentication flow becomes more explicit and easier to manage. Thank you for catching this omissi`AuthResultState`**: A sealed class to represent the different states of the authentication flow. This makes it easy for the UI to react to chang