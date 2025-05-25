package com.shubhanya.fingenienxt.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhanya.fingenienxt.auth.AuthRepository
import com.shubhanya.fingenienxt.data.local.entity.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository // Inject AuthRepository
) : ViewModel() {

    val profileSaveState: StateFlow<ProfileSaveState> = profileRepository.profileSaveState
    val userProfile: StateFlow<UserProfile?> = profileRepository.userProfile

    fun saveUserProfile(name: String, dob: String) {
        if (name.isBlank() || dob.isBlank()) {
            // Update state for UI feedback
            viewModelScope.launch {
                profileRepository._profileSaveState.value = ProfileSaveState.Error("Name and Date of Birth cannot be empty.")
            }
            return
        }
        viewModelScope.launch {
            val success = profileRepository.saveUserProfile(name, dob) // Call SUSPEND function
            if (success) {
                // Profile saved, now re-check auth state to transition from NewUser to Success
                authRepository.getCurrentUser()?.uid?.let {
                    // This call will update AuthViewModel's observed authResult
                    authRepository.checkIfUserProfileExistsAndUpdateAuthState(it)
                }
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            profileRepository.fetchUserProfileData() // Call SUSPEND function
        }
    }

    fun updateLastLogin() {
        viewModelScope.launch {
            profileRepository.updateLastLoginTimestamp() // Call SUSPEND function
        }
    }

    fun resetProfileSaveState() {
        profileRepository.resetProfileSaveStateToIdle()
    }
}

// Sealed class ProfileSaveState (ensure this is defined)
// object Idle : ProfileSaveState()
// object Loading : ProfileSaveState()
// object Success : ProfileSaveState()
// data class Error(val message: String) : ProfileSaveState()