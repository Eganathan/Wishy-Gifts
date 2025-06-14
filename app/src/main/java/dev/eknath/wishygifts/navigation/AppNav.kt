package dev.eknath.wishygifts.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.eknath.wishygifts.auth.rememberAuthState
import dev.eknath.wishygifts.auth.ui.AuthNavigation

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val userState = rememberAuthState()
    if (userState.value == null) {
        AuthNavigation()
    } else {
        DynamicNavigation3(modifier)
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun DynamicNavigation3(modifier: Modifier = Modifier) {
    val listDetailPaneScaffoldNavigator = rememberListDetailPaneScaffoldNavigator()
    val backStack = remember { mutableStateListOf<Any>("") }
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { route ->
            when (route) {
                else -> NavEntry(Unit) { Text("Unknown route: $route") }
            }
        }
    )
}