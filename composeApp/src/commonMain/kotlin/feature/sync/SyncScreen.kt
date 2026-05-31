package feature.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Auth / account sync screen. Zero business logic — dispatches Intents only. */
@Composable
fun SyncScreen(viewModel: SyncViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO (Feature e): render sign-in options, signed-in state, sign-out, account deletion
}
