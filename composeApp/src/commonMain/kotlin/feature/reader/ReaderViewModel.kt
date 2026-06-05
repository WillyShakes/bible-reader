package feature.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.rema.bible.shared.AppPreferences
import domain.model.CanonicalBooks
import domain.model.enums.Language
import domain.usecase.GetBibleBookUseCase
import domain.usecase.GetBibleChapterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Drives the Bible reader screen. */
class ReaderViewModel(
    private val getBibleChapterUseCase: GetBibleChapterUseCase,
    private val getBibleBookUseCase: GetBibleBookUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReaderUiState(language = AppPreferences.getPreferredLanguage())
    )
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(bookList = getBibleBookUseCase()) }
        loadChapter(bookId = "GEN", chapter = 1)
    }

    fun onIntent(intent: ReaderIntent) {
        when (intent) {
            is ReaderIntent.NavigateToChapter -> loadChapter(intent.bookId, intent.chapter)
            is ReaderIntent.SelectVerse -> _uiState.update { it.copy(selectedVerseNumber = intent.verseNumber) }
            is ReaderIntent.ClearVerseSelection -> _uiState.update { it.copy(selectedVerseNumber = null) }
            is ReaderIntent.SwitchTranslation -> switchTranslation(intent.translation)
            is ReaderIntent.NavigateToNextChapter -> navigateNextChapter()
            is ReaderIntent.NavigateToPreviousChapter -> navigatePreviousChapter()
            is ReaderIntent.OpenBookList -> _uiState.update { it.copy(showBookList = true) }
            is ReaderIntent.DismissBookList -> _uiState.update { it.copy(showBookList = false) }
        }
    }

    private fun loadChapter(bookId: String, chapter: Int) {
        val book = CanonicalBooks.findById(bookId) ?: return
        val bookName = if (_uiState.value.language == Language.FR) book.nameFr else book.nameEn
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    bookId = bookId,
                    chapter = chapter,
                    bookName = bookName,
                    totalChapters = book.totalChapters,
                    selectedVerseNumber = null,
                )
            }
            getBibleChapterUseCase(_uiState.value.translation, bookId, chapter)
                .collect { verses ->
                    _uiState.update { it.copy(verses = verses, isLoading = false) }
                }
        }
    }

    private fun switchTranslation(translation: domain.model.enums.Translation) {
        _uiState.update { it.copy(translation = translation) }
        loadChapter(_uiState.value.bookId, _uiState.value.chapter)
    }

    private fun navigateNextChapter() {
        val state = _uiState.value
        if (state.chapter < state.totalChapters) {
            loadChapter(state.bookId, state.chapter + 1)
        } else {
            val nextBook = CanonicalBooks.nextBook(state.bookId) ?: return
            loadChapter(nextBook.bookId, 1)
        }
    }

    private fun navigatePreviousChapter() {
        val state = _uiState.value
        if (state.chapter > 1) {
            loadChapter(state.bookId, state.chapter - 1)
        } else {
            val prevBook = CanonicalBooks.previousBook(state.bookId) ?: return
            loadChapter(prevBook.bookId, prevBook.totalChapters)
        }
    }
}