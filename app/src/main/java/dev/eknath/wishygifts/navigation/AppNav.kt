package dev.eknath.wishygifts.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
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
import dev.eknath.wishygifts.auth.ui.AuthNavigation
import dev.eknath.wishygifts.auth.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNav(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val uiState by authViewModel.uiState.collectAsState()

//    if (!uiState.isAuthenticated) {
//        // Show authentication flow if user is not logged in
        AuthNavigation(
            modifier = modifier,
            authViewModel = authViewModel,
            authenticatedContent = {
                DynamicNavigation3(modifier, authViewModel)
            }
        )
//    } else {
//        // User is already authenticated, show the authenticated content directly
//        DynamicNavigation3(modifier, authViewModel)
//    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun DynamicNavigation3(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val listDetailPaneScaffoldNavigator = rememberListDetailPaneScaffoldNavigator()
    val backStack = remember { mutableStateListOf<Any>("home") }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { route ->
            when (route) {
                "home" -> NavEntry(Unit) {
                    AuthenticatedHomeScreen(
                        onSignOut = {
                            authViewModel.signOut()
                        }
                    )
                }

                else -> NavEntry(Unit) { Text("Unknown route: $route") }
            }
        }
    )
}

@Composable
fun AuthenticatedHomeScreen(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to DynamicNavigation3!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You are now authenticated and viewing the test UI.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text("Sign Out")
        }
    }
}
