package feature.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.GetBibleChapterUseCase
import domain.usecase.SaveDayCompleteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Drives the Bible reader screen. */
class ReaderViewModel(
    private val getBibleChapterUseCase: GetBibleChapterUseCase,
    private val saveDayCompleteUseCase: SaveDayCompleteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ReaderIntent) {
        when (intent) {
            is ReaderIntent.NavigateToChapter -> loadChapter(intent.bookId, intent.chapter)
            is ReaderIntent.SelectVerse -> _uiState.update { it.copy(selectedVerseNumber = intent.verseNumber) }
            is ReaderIntent.ClearVerseSelection -> _uiState.update { it.copy(selectedVerseNumber = null) }
            is ReaderIntent.SwitchTranslation -> switchTranslation(intent)
            is ReaderIntent.NavigateToNextChapter -> TODO("Feature a")
            is ReaderIntent.NavigateToPreviousChapter -> TODO("Feature a")
        }
    }

    private fun loadChapter(bookId: String, chapter: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, bookId = bookId, chapter = chapter) }
            getBibleChapterUseCase(_uiState.value.translation, bookId, chapter)
                .collect { verses ->
                    _uiState.update { it.copy(verses = verses, isLoading = false) }
                }
        }
    }

    private fun switchTranslation(intent: ReaderIntent.SwitchTranslation) {
        _uiState.update { it.copy(translation = intent.translation) }
        loadChapter(_uiState.value.bookId, _uiState.value.chapter)
    }
}
