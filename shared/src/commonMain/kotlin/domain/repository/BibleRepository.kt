package domain.repository

import domain.model.BibleVerse
import domain.model.enums.Translation
import kotlinx.coroutines.flow.Flow

/**
 * Read-only access to the bundled Bible text.
 * All queries are served from the local SQLDelight database — no network calls.
 */
interface BibleRepository {
    /** Emits all verses for the given chapter, ordered by verse number. */
    fun observeChapter(translation: Translation, bookId: String, chapter: Int): Flow<List<BibleVerse>>

    /** Returns a single verse, or null if not found. */
    suspend fun getVerse(translation: Translation, bookId: String, chapter: Int, verse: Int): BibleVerse?
}
