package dev.eknath.wishygifts.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    landingUI: @Composable () -> Unit = {},
    onLoginSuccess: (email: String, password: String) -> Unit = { _, _ -> },
    onSignUpSuccess: (email: String, password: String) -> Unit = { _, _ -> }
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "landing",
        modifier = modifier
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
                        onLoginSuccess(email, password)
                    },
                    onSignUpClick = { navController.navigate("signup") },
                    onForgotPasswordClick = { /* TODO: Implement forgot password flow */ }
                )
            }
        )

        composable(
            route = "signup",
            content = {
                SignUpScreen(
                    modifier = Modifier,
                    onSignUpClick = { email, password -> 
                        onSignUpSuccess(email, password)
                    },
                    onLoginClick = { navController.navigate("login") }
                )
            }
        )
    }
}
