package dev.eknath.wishygifts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dev.eknath.wishygifts.auth.Auth
import dev.eknath.wishygifts.auth.data.firebase.FireStoreUserDB
import dev.eknath.wishygifts.auth.viewmodel.AuthViewModel
import dev.eknath.wishygifts.navigation.AppNav
import dev.eknath.wishygifts.ui.theme.WishyGiftsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Auth.init()
        enableEdgeToEdge()
        setContent {
            val authVM by viewModels<AuthViewModel>()
            WishyGiftsTheme {
                AppNav(
                    authViewModel = authVM
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Release Firestore instance to prevent memory leaks
        FireStoreUserDB.releaseFirestoreInstance()
    }
}
