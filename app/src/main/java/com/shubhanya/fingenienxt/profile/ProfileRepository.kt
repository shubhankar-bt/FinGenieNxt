package com.shubhanya.fingenienxt.profile


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhanya.fingenienxt.data.local.entity.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val TAG = "ProfileRepository"
    private val usersCollection = firestore.collection("users")

    // Internal MutableStateFlow - updated directly
    internal val _profileSaveState = MutableStateFlow<ProfileSaveState>(ProfileSaveState.Idle)
    val profileSaveState: StateFlow<ProfileSaveState> = _profileSaveState

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    // SUSPEND FUNCTION: Called from ViewModel's viewModelScope
    suspend fun saveUserProfile(name: String, dob: String): Boolean {
        _profileSaveState.value = ProfileSaveState.Loading
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _profileSaveState.value = ProfileSaveState.Error("User not logged in.")
            return false
        }

        val userProfileData = UserProfile(
            uid = currentUser.uid,
            phoneNumber = currentUser.phoneNumber ?: "",
            name = name,
            dob = dob,
            // createdAt will be set by @ServerTimestamp by Firestore
            lastLogin = Date() // Set current time for last login during profile creation
        )

        return try {
            usersCollection.document(currentUser.uid).set(userProfileData).await()
            _profileSaveState.value = ProfileSaveState.Success
            // Fetch the profile again to update the local StateFlow
            fetchUserProfileData() // Update local cache
            Log.d(TAG, "User profile saved successfully for UID: ${currentUser.uid}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile", e)
            _profileSaveState.value = ProfileSaveState.Error("Failed to save profile: ${e.message ?: "Unknown error"}")
            false
        }
    }

    // SUSPEND FUNCTION: Called from ViewModel's viewModelScope
    // Renamed to avoid confusion with the StateFlow `userProfile`
    suspend fun fetchUserProfileData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _userProfile.value = null
            return
        }
        try {
            val documentSnapshot = usersCollection.document(currentUser.uid).get().await()
            if (documentSnapshot.exists()) {
                val profile = documentSnapshot.toObject(UserProfile::class.java)
                _userProfile.value = profile
            } else {
                _userProfile.value = null // Profile doesn't exist
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user profile", e)
            _userProfile.value = null // Error case
        }
    }

    // SUSPEND FUNCTION: Called from ViewModel's viewModelScope
    suspend fun updateLastLoginTimestamp() {
        val currentUser = auth.currentUser
        currentUser?.uid?.let { uid ->
            try {
                val newLoginTime = Date()
                usersCollection.document(uid).update("lastLogin", newLoginTime).await()
                Log.d(TAG, "Last login timestamp updated for UID: $uid")
                // Update local cache if profile is already loaded
                _userProfile.value?.let {
                    _userProfile.value = it.copy(lastLogin = newLoginTime)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating last login timestamp", e)
            }
        }
    }

    fun resetProfileSaveStateToIdle() {
        _profileSaveState.value = ProfileSaveState.Idle
    }
}