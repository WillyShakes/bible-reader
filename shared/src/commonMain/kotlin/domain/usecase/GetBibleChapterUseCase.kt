package domain.usecase

import domain.model.BibleVerse
import domain.model.enums.Translation
import domain.repository.BibleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Returns the verses for a single chapter as a reactive stream.
 * Served entirely from local SQLDelight — no network call.
 */
class GetBibleChapterUseCase(private val bibleRepository: BibleRepository) {
    operator fun invoke(translation: Translation, bookId: String, chapter: Int): Flow<List<BibleVerse>> =
        bibleRepository.observeChapter(translation, bookId, chapter)
}
