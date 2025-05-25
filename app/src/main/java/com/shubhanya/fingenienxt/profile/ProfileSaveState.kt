package com.shubhanya.fingenienxt.profile

// --- Sealed class for Profile Save State ---
sealed class ProfileSaveState {
    object Idle : ProfileSaveState()
    object Loading : ProfileSaveState()
    object Success : ProfileSaveState()
    data class Error(val message: String) : ProfileSaveState()
}


/**
 * **Explanation:**
 * * **`ProfileRepository`**:
 *     * Handles saving and fetching `UserProfile` data to/from Firestore's `users` collection.
 *     * `saveUserProfile()`: Creates or updates the user's profile document.
 *     * `fetchUserProfile()`: Retrieves the current user's profile.
 *     * `updateLastLoginTimestamp()`: Updates a timestamp when the user logs in.
 * * **`ProfileViewModel`**:
 *     * Manages UI-related data for the profile screen.
 *     * `saveUserProfile()`: Validates input and calls the repository.
 * * **`ProfileSaveState`**: Represents the state of the profile saving operati
 */