package dev.eknath.wishygifts.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    landingUI: @Composable () -> Unit = {}
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
    }
}