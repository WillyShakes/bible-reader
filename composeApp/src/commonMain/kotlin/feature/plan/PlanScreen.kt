package feature.plan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Reading plan home screen. Zero business logic — dispatches Intents only. */
@Composable
fun PlanScreen(viewModel: PlanViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO (Feature b): render today's assignment, progress indicator, mark-as-read CTA
}
