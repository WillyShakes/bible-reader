package feature.sync

import androidx.lifecycle.ViewModel
import domain.usecase.GetActivePlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Drives the auth / account sync screen. */
class SyncViewModel(
    private val getActivePlanUseCase: GetActivePlanUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncUiState())
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()

    fun onIntent(intent: SyncIntent) {
        when (intent) {
            is SyncIntent.SignInWithApple -> TODO("Feature e")
            is SyncIntent.SignInWithGoogle -> TODO("Feature e")
            is SyncIntent.SignInWithEmail -> TODO("Feature e")
            is SyncIntent.SignOut -> TODO("Feature e")
            is SyncIntent.DeleteAccount -> TODO("Feature e — AC-E-9 hard-delete")
            is SyncIntent.ConfirmDeleteAccount -> TODO("Feature e — AC-E-9 hard-delete")
        }
    }
}
