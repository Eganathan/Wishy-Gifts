package dev.eknath.wishygifts.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.eknath.wishygifts.auth.viewmodel.AuthViewModel
import dev.eknath.wishygifts.ui.screens.settings.SettingsScreen


sealed interface AppScreen {
    data object Home : AppScreen
    data object Settings : AppScreen
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AuthenticatedNav(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val backStack = remember { mutableStateListOf<AppScreen>(AppScreen.Settings) }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { route ->
            when (route) {
                is AppScreen.Settings -> NavEntry(AppScreen.Settings) {
                    SettingsScreen(
                        onSignOut = {
                            authViewModel.signOut()
                        }
                    )
                }

                AppScreen.Home -> TODO()
            }
        }
    )
}