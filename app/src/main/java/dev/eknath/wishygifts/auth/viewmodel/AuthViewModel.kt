package dev.eknath.wishygifts.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eknath.wishygifts.auth.Auth
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
        val isSuccessful: Boolean = false
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Check if user is already authenticated
        _uiState.value = _uiState.value.copy(
            isAuthenticated = Auth.getCurrentUser() != null
        )
    }

    /**
     * Sign in with email and password
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                Auth.signIn(email, password).await()
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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
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
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            isSuccessful = false
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
}