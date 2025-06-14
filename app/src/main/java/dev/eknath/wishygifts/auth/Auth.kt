package dev.eknath.wishygifts.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object Auth {
    private lateinit var auth: FirebaseAuth

    fun init() {
        auth = FirebaseAuth.getInstance()
    }

    /**
     * Signs in a user with email and password
     * @param email User's email
     * @param password User's password
     * @return Task<AuthResult> that can be used to check success or failure
     */
    fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    /**
     * Creates a new user account with email and password
     * @param email User's email
     * @param password User's password
     * @return Task<AuthResult> that can be used to check success or failure
     */
    fun signUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    /**
     * Sends a password reset email to the specified email address
     * @param email Email address to send the password reset link to
     * @return Task<Void> that can be used to check success or failure
     */
    fun forgotPassword(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    /**
     * Signs out the current user
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Gets the current authenticated user
     * @return FirebaseUser? The current user or null if not authenticated
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}


@Composable
fun rememberAuthState(): State<FirebaseUser?> {
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val userState = remember { mutableStateOf(firebaseAuth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            userState.value = auth.currentUser
        }
        firebaseAuth.addAuthStateListener(listener)

        onDispose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    return userState
}