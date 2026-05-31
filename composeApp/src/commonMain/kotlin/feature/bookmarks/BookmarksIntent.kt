package feature.bookmarks

import domain.model.Bookmark

/** All user actions on the bookmarks screen. */
sealed interface BookmarksIntent {
    data class SelectBookmark(val bookmark: Bookmark) : BookmarksIntent
    data class DeleteBookmark(val bookmarkId: String) : BookmarksIntent
    data class ShareBookmarkAsText(val bookmark: Bookmark) : BookmarksIntent
    data class ShareBookmarkAsCard(val bookmark: Bookmark) : BookmarksIntent
}
