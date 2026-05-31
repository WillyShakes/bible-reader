package feature.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Bible reader screen. Zero business logic — dispatches Intents only. */
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO (Feature a): render chapter, verse selection, translation toggle, prev/next nav
}
