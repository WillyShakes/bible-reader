package feature.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Drives the notification preferences screen. */
class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun onIntent(intent: NotificationsIntent) {
        when (intent) {
            is NotificationsIntent.SetDailyReminderEnabled -> TODO("Feature d")
            is NotificationsIntent.SetDailyReminderTime -> TODO("Feature d")
            is NotificationsIntent.SetReEngagementEnabled -> TODO("Feature d")
        }
    }
}
