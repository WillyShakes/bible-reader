package feature.catchup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Catch-up / grace mechanic screen. Zero business logic — dispatches Intents only. */
@Composable
fun CatchUpScreen(viewModel: CatchUpViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO (Feature c): render available strategy options, schedule preview, confirm/dismiss
}
