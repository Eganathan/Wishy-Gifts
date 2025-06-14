package dev.eknath.wishygifts.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.eknath.wishygifts.auth.ui.AuthNavigation
import dev.eknath.wishygifts.auth.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNav(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    AuthNavigation(
        modifier = modifier,
        authViewModel = authViewModel,
        authenticatedContent = {
            AuthenticatedNav(modifier, authViewModel)
        }
    )
}
