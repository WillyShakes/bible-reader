package feature.bookmarks

import domain.model.Bookmark

/** Single source of truth for the bookmarks list and sharing screen. */
data class BookmarksUiState(
    val bookmarks: List<Bookmark> = emptyList(),
    val selectedBookmark: Bookmark? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
