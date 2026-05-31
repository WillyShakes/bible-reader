package feature.sync

/** All user actions on the auth / account sync screen. */
sealed interface SyncIntent {
    data object SignInWithApple : SyncIntent
    data object SignInWithGoogle : SyncIntent
    data class SignInWithEmail(val email: String, val password: String) : SyncIntent
    data object SignOut : SyncIntent
    data object DeleteAccount : SyncIntent
    data object ConfirmDeleteAccount : SyncIntent
}
