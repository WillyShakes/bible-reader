package feature.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Bookmarks list screen. Zero business logic — dispatches Intents only. */
@Composable
fun BookmarksScreen(viewModel: BookmarksViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO (Feature f): render bookmark list, selection, share/delete actions
}
