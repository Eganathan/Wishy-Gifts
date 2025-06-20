package dev.eknath.wishygifts.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eknath.wishygifts.auth.Auth
import dev.eknath.wishygifts.auth.data.firebase.FireStoreUserDB
import dev.eknath.wishygifts.auth.data.firebase.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for handling authentication operations and state
 */
class AuthViewModel : ViewModel() {

    // UI state for authentication
    data class AuthUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isAuthenticated: Boolean = false,
        val isSuccessful: Boolean = false,
        val userProfile: User? = null,
        val hasProfile: Boolean = false,
        val needsProfileUpdate: Boolean = false
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Check if user is already authenticated
        val isAuthenticated = Auth.getCurrentUser() != null
        _uiState.value = _uiState.value.copy(
            isAuthenticated = isAuthenticated
        )

        // If authenticated, check if user has a profile
        if (isAuthenticated) {
            checkUserProfile()
        }
    }

    /**
     * Check if the current user has a profile and update UI state accordingly
     */
    private fun checkUserProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Check if profile exists
                val profileExists = FireStoreUserDB.userProfileExistsAsync()

                if (profileExists) {
                    // Get user profile
                    val userProfile = FireStoreUserDB.getUserProfile()

                    // Check if display name is empty
                    val needsProfileUpdate = userProfile?.displayName.isNullOrEmpty()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = userProfile,
                        hasProfile = true,
                        needsProfileUpdate = needsProfileUpdate
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasProfile = false,
                        needsProfileUpdate = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to check user profile"
                )
            }
        }
    }

    /**
     * Sign in with email and password
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                Auth.signIn(email, password).await()

                // Check if user has a profile
                checkUserProfile()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    isSuccessful = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Sign in failed"
                )
            }
        }
    }

    /**
     * Sign up with email and password
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                Auth.signUp(email, password).await()

                // After signup, user needs to create a profile
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    hasProfile = false,
                    needsProfileUpdate = true,
                    isSuccessful = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Sign up failed"
                )
            }
        }
    }

    /**
     * Send password reset email
     */
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                Auth.forgotPassword(email).await()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccessful = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to send reset email"
                )
            }
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        Auth.signOut()
        // Release Firestore instance to prevent memory leaks
        FireStoreUserDB.releaseFirestoreInstance()
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            isSuccessful = false,
            userProfile = null,
            hasProfile = false,
            needsProfileUpdate = false
        )
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Reset success state after handling it
     */
    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(isSuccessful = false)
    }

    /**
     * Create or update user profile
     */
    fun updateUserProfile(displayName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                val currentUser = Auth.getCurrentUser()
                if (currentUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User not authenticated"
                    )
                    return@launch
                }

                // Create user object
                val user = User(
                    displayName = displayName,
                    email = currentUser.email ?: "",
                    photoUrl = currentUser.photoUrl?.toString() ?: ""
                )

                // Check if profile exists
                val profileExists = FireStoreUserDB.userProfileExistsAsync()

                val success = if (profileExists) {
                    // Update existing profile
                    FireStoreUserDB.updateUserProfileAsync(user)
                } else {
                    // Create new profile
                    FireStoreUserDB.createUserProfileAsync(user)
                }

                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = user,
                        hasProfile = true,
                        needsProfileUpdate = false,
                        isSuccessful = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to update profile"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to update profile"
                )
            }
        }
    }

    /**
     * Check if the user needs to update their profile
     */
    fun checkProfileUpdateNeeded() {
        if (_uiState.value.isAuthenticated) {
            checkUserProfile()
        }
    }
}
