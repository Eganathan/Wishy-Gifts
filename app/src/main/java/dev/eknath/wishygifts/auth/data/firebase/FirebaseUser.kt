package dev.eknath.wishygifts.auth.data.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.eknath.wishygifts.auth.Auth
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
)


object FireStoreUserDB {
    private var firestoreInstance: FirebaseFirestore? = null

    private val firestore: FirebaseFirestore
        get() {
            if (firestoreInstance == null) {
                firestoreInstance = FirebaseFirestore.getInstance()
            }
            return firestoreInstance!!
        }

    /**
     * Release the Firestore instance to prevent memory leaks
     * Call this method when the app is going to background or when the user logs out
     */
    fun releaseFirestoreInstance() {
        firestoreInstance = null
    }

    /**
     * Gets the document reference for the current user's profile
     * @return DocumentReference or null if user is not authenticated
     */
    private fun getUserDocRef(): DocumentReference? {
        val currentUser = Auth.getCurrentUser()
        return if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
        } else {
            null
        }
    }

    /**
     * Retrieves the user profile from Firestore
     * @return User object if successful, null if not found or error occurs
     */
    suspend fun getUserProfile(): User? {
        return try {
            val docRef = getUserDocRef() ?: return null
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                Log.d("UserProfile", snapshot.toString())
                snapshot.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error getting user profile: ${e.message}")
            null
        }
    }

    /**
     * Updates the user profile in Firestore
     * @param user User object with updated information
     * @return Task<Void> that can be used to check success or failure, or null if user is not authenticated
     */
    fun updateUserProfile(user: User): Task<Void>? {
        val docRef = getUserDocRef() ?: return null
        return docRef.set(user)
    }

    /**
     * Creates a new user profile in Firestore if it doesn't exist
     * @param user User object with initial information
     * @return Task<Void> that can be used to check success or failure, or null if user is not authenticated
     */
    fun createUserProfile(user: User): Task<Void>? {
        val docRef = getUserDocRef() ?: return null
        return docRef.set(user)
    }

    /**
     * Checks if a user profile exists in Firestore
     * @return Task<Boolean> that resolves to true if profile exists, false otherwise
     */
    fun userProfileExists(): Task<Boolean>? {
        val docRef = getUserDocRef() ?: return null
        return docRef.get().continueWith { task ->
            if (task.isSuccessful) {
                task.result?.exists() ?: false
            } else {
                false
            }
        }
    }

    /**
     * Updates specific fields of the user profile
     * @param updates Map of field names to new values
     * @return Task<Void> that can be used to check success or failure, or null if user is not authenticated
     */
    fun updateUserFields(updates: Map<String, Any>): Task<Void>? {
        val docRef = getUserDocRef() ?: return null
        return docRef.update(updates)
    }

    /**
     * Suspend version of userProfileExists
     * @return true if profile exists, false otherwise or if user is not authenticated
     */
    suspend fun userProfileExistsAsync(): Boolean {
        val docRef = getUserDocRef() ?: return false
        return try {
            val snapshot = docRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            println("Error checking if user profile exists: ${e.message}")
            false
        }
    }

    /**
     * Suspend version of updateUserProfile
     * @param user User object with updated information
     * @return true if update was successful, false otherwise or if user is not authenticated
     */
    suspend fun updateUserProfileAsync(user: User): Boolean {
        val docRef = getUserDocRef() ?: return false
        return try {
            docRef.set(user).await()
            true
        } catch (e: Exception) {
            println("Error updating user profile: ${e.message}")
            false
        }
    }

    /**
     * Suspend version of updateUserFields
     * @param updates Map of field names to new values
     * @return true if update was successful, false otherwise or if user is not authenticated
     */
    suspend fun updateUserFieldsAsync(updates: Map<String, Any>): Boolean {
        val docRef = getUserDocRef() ?: return false
        return try {
            docRef.update(updates).await()
            true
        } catch (e: Exception) {
            println("Error updating user fields: ${e.message}")
            false
        }
    }

    /**
     * Suspend version of createUserProfile
     * @param user User object with initial information
     * @return true if creation was successful, false otherwise or if user is not authenticated
     */
    suspend fun createUserProfileAsync(user: User): Boolean {
        val docRef = getUserDocRef() ?: return false
        return try {
            docRef.set(user).await()
            true
        } catch (e: Exception) {
            println("Error creating user profile: ${e.message}")
            false
        }
    }
}
