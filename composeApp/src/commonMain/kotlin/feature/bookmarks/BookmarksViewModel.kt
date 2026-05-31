package feature.bookmarks

import androidx.lifecycle.ViewModel
import domain.usecase.GetBookmarksUseCase
import domain.usecase.SaveDayCompleteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Drives the bookmarks list and sharing screen. */
class BookmarksViewModel(
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val saveDayCompleteUseCase: SaveDayCompleteUseCase,
) : ViewModel() {

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
