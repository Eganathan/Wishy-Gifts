package dev.eknath.wishygifts.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.eknath.wishygifts.auth.viewmodel.AuthViewModel
import dev.eknath.wishygifts.ui.screens.settings.SettingsScreen


sealed interface AppScreen {
    data object Home : AppScreen
    data object Settings : AppScreen
    data object EditProfile : AppScreen
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
                        authViewModel = authViewModel,
                        onSignOut = {
                            authViewModel.signOut()
                        },
                        onEditProfile = {
                            backStack.add(AppScreen.EditProfile)
                        }
                    )
                }

                is AppScreen.EditProfile -> NavEntry(AppScreen.EditProfile) {
                    // Placeholder for the EditProfileScreen
                    val uiState by authViewModel.uiState.collectAsState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display current profile info
                        uiState.userProfile?.let { profile ->
                            Text(
                                text = "Current Display Name: ${profile.displayName}",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Email: ${profile.email}",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Back button
                        Button(
                            onClick = { backStack.removeLastOrNull() },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Back to Settings")
                        }
                    }
                }

                AppScreen.Home -> TODO()
            }
        }
    )
}
