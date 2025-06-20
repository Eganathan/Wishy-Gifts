package dev.eknath.wishygifts.ui.screens.settings

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.eknath.wishygifts.auth.viewmodel.AuthViewModel


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit,
    onEditProfile: () -> Unit = {}
) {
    val uiState by authViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display user's name if available
        uiState.userProfile?.displayName?.let { displayName ->
            Text(
                text = "Hello, $displayName!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        } ?: Text(
            text = "Welcome to WishyGifts!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You are now authenticated and viewing the settings screen.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Edit Profile Button
        Button(
            onClick = onEditProfile,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
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
