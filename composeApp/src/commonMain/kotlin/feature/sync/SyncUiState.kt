package feature.sync

import domain.model.enums.AuthProvider

/** Single source of truth for the auth / account sync screen. */
data class SyncUiState(
    val isSignedIn: Boolean = false,
    val provider: AuthProvider = AuthProvider.GUEST,
    val displayName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
