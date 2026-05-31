package feature.reader

import domain.model.BibleVerse
import domain.model.enums.Translation

/** Single source of truth for the Bible reader screen. */
data class ReaderUiState(
    val translation: Translation = Translation.LOUIS_SEGOND,
    val bookId: String = "",
    val chapter: Int = 1,
    val verses: List<BibleVerse> = emptyList(),
    val selectedVerseNumber: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
