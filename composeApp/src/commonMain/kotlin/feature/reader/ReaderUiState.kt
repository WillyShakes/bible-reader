package feature.reader

import domain.model.BibleBook
import domain.model.BibleVerse
import domain.model.enums.Language
import domain.model.enums.Translation

/** Single source of truth for the Bible reader screen. */
data class ReaderUiState(
    val translation: Translation = Translation.LOUIS_SEGOND,
    val language: Language = Language.FR,
    val bookId: String = "GEN",
    val chapter: Int = 1,
    val bookName: String = "",
    val totalChapters: Int = 1,
    val verses: List<BibleVerse> = emptyList(),
    val selectedVerseNumber: Int? = null,
    val bookList: List<BibleBook> = emptyList(),
    val showBookList: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)