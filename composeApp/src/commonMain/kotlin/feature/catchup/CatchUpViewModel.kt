package feature.catchup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Drives the catch-up / grace mechanic screen. */
class CatchUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatchUpUiState())
    val uiState: StateFlow<CatchUpUiState> = _uiState.asStateFlow()

    fun onIntent(intent: CatchUpIntent) {
        when (intent) {
            is CatchUpIntent.SelectStrategy -> TODO("Feature c")
            is CatchUpIntent.Confirm -> TODO("Feature c")
            is CatchUpIntent.Dismiss -> _uiState.update { it.copy(showCatchUp = false) }
        }
    }
}