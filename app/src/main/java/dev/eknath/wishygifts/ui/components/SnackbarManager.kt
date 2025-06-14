package dev.eknath.wishygifts.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A utility class to manage Snackbar display in Compose
 */
class SnackbarManager(
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope
) {
    /**
     * Show a snackbar with the given message
     * @param message The message to display
     * @param actionLabel Optional label for the action button
     * @param duration How long to display the snackbar
     * @param onAction Callback when the action is clicked
     * @param onDismiss Callback when the snackbar is dismissed
     */
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
            
            when (result) {
                SnackbarResult.ActionPerformed -> onAction?.invoke()
                SnackbarResult.Dismissed -> onDismiss?.invoke()
            }
        }
    }
    
    /**
     * Show an error snackbar
     */
    fun showError(
        message: String,
        actionLabel: String? = "Dismiss",
        onAction: (() -> Unit)? = null
    ) {
        showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long,
            onAction = onAction
        )
    }
}

/**
 * Remember a SnackbarManager instance
 */
@Composable
fun rememberSnackbarManager(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): SnackbarManager {
    return remember(snackbarHostState, coroutineScope) {
        SnackbarManager(snackbarHostState, coroutineScope)
    }
}

/**
 * A composable that shows a snackbar when an error message is provided
 */
@Composable
fun ErrorSnackbar(
    errorMessage: String?,
    snackbarHostState: SnackbarHostState,
    onErrorShown: () -> Unit
) {
    errorMessage?.let {
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Long
            )
            onErrorShown()
        }
    }
}

/**
 * Remember a coroutine scope that follows the Composable lifecycle
 */
@Composable
fun rememberCoroutineScope(): CoroutineScope {
    return androidx.compose.runtime.rememberCoroutineScope()
}