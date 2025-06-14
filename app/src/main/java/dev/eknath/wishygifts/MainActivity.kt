package dev.eknath.wishygifts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.eknath.wishygifts.navigation.AppNav
import dev.eknath.wishygifts.ui.theme.WishyGiftsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WishyGiftsTheme {
                AppNav()
            }
        }
    }
}