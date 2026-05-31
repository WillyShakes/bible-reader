package domain.usecase

import domain.model.Bookmark
import domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

/** Returns the user's bookmarks as a reactive stream, in reverse-chronological order. */
class GetBookmarksUseCase(private val bookmarkRepository: BookmarkRepository) {
    operator fun invoke(uid: String): Flow<List<Bookmark>> =
        bookmarkRepository.observeBookmarks(uid)
}
