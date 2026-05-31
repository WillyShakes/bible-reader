package feature.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Notification preferences screen. Zero business logic — dispatches Intents only. */
@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO (Feature d): render time picker, enable/disable toggles, re-engagement toggle
}
