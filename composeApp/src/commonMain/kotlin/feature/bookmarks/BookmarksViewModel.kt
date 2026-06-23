package feature.bookmarks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Drives the bookmarks list and sharing screen. */
class BookmarksViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    fun onIntent(intent: BookmarksIntent) {
        when (intent) {
            is BookmarksIntent.SelectBookmark -> TODO("Feature f")
            is BookmarksIntent.DeleteBookmark -> TODO("Feature f")
            is BookmarksIntent.ShareBookmarkAsText -> TODO("Feature f")
            is BookmarksIntent.ShareBookmarkAsCard -> TODO("Feature f")
        }
    }
}