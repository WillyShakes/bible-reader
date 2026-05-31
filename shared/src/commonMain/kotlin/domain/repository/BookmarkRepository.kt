package domain.repository

import domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

/**
 * Manages verse bookmarks.
 * Available fully offline; synced to Firestore when signed in.
 */
interface BookmarkRepository {
    /** Emits the user's bookmarks in reverse-chronological order, excluding soft-deleted entries. */
    fun observeBookmarks(uid: String): Flow<List<Bookmark>>

    /** Saves a new bookmark or updates an existing one. */
    suspend fun saveBookmark(bookmark: Bookmark): Result<Unit>

    /**
     * Soft-deletes a bookmark for sync propagation.
     * Hard-delete happens only during account deletion (AC-E-9).
     */
    suspend fun deleteBookmark(bookmarkId: String): Result<Unit>

    /** Hard-deletes all bookmarks for the user. Called only during account deletion. */
    suspend fun deleteAllBookmarksForUser(uid: String): Result<Unit>
}
