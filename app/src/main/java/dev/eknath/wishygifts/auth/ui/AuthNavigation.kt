package dev.eknath.wishygifts.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.eknath.wishygifts.auth.viewmodel.AuthViewModel
import dev.eknath.wishygifts.navigation.AuthenticatedHomeScreen
import dev.eknath.wishygifts.ui.components.ErrorSnackbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    landingUI: @Composable () -> Unit = {},
    authViewModel: AuthViewModel,
    authenticatedContent: @Composable () -> Unit
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by authViewModel.uiState.collectAsState()

    // Handle successful authentication
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            // Navigate to authenticated route
            navController.navigate("authenticated") {
                // Clear the back stack so user can't go back to auth screens
                popUpTo("landing") { inclusive = true }
            }
        }
    }

    // Handle successful operations
    LaunchedEffect(uiState.isSuccessful) {
        if (uiState.isSuccessful) {
            // If we're on the forgot password screen, navigate back to login
            if (navController.currentBackStackEntry?.destination?.route == "forgot_password") {
                navController.navigate("login")
            }
            // Reset success state after handling
            authViewModel.resetSuccessState()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Show error messages in a Snackbar
            ErrorSnackbar(
                errorMessage = uiState.errorMessage,
                snackbarHostState = snackbarHostState,
                onErrorShown = { authViewModel.clearError() }
            )

            NavHost(
                navController = navController,
                startDestination = if (uiState.isAuthenticated) {
                    "authenticated"
                } else {
                    "landing"
                },
                modifier = Modifier.fillMaxSize()
            ) {
                composable(
                    route = "landing",
                    content = {
                        LandingScreen(
                            modifier = Modifier,
                            onSignUpClick = { navController.navigate("signup") },
                            onLoginClick = { navController.navigate("login") }
                        )
                    }
                )

                composable(
                    route = "login",
                    content = {
                        LoginScreen(
                            modifier = Modifier,
                            onLoginClick = { email, password ->
                                authViewModel.signIn(email, password)
                            },
                            onSignUpClick = { navController.navigate("signup") },
                            onForgotPasswordClick = { navController.navigate("forgot_password") }
                        )
                    }
                )

                composable(
                    route = "signup",
                    content = {
                        SignUpScreen(
                            modifier = Modifier,
                            onSignUpClick = { email, password ->
                                authViewModel.signUp(email, password)
                            },
                            onLoginClick = { navController.navigate("login") }
                        )
                    }
                )

                composable(
                    route = "forgot_password",
                    content = {
                        ForgotPasswordScreen(
                            onResetPasswordClick = { email ->
                                authViewModel.forgotPassword(email)
                                // Navigation will be handled by LaunchedEffect when isSuccessful becomes true
                            },
                            onBackClick = { navController.navigateUp() }
                        )
                    }
                )

                composable(route = "authenticated") {
                    authenticatedContent()
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
