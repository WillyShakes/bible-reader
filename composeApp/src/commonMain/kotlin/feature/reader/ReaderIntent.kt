package feature.reader

import domain.model.enums.Translation

/** All user actions in the Bible reader screen. */
sealed interface ReaderIntent {
    data class NavigateToChapter(val bookId: String, val chapter: Int) : ReaderIntent
    data class SelectVerse(val verseNumber: Int) : ReaderIntent
    data object ClearVerseSelection : ReaderIntent
    data class SwitchTranslation(val translation: Translation) : ReaderIntent
    data object NavigateToNextChapter : ReaderIntent
    data object NavigateToPreviousChapter : ReaderIntent
}
