package feature.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import biblereader.composeapp.generated.resources.Res
import biblereader.composeapp.generated.resources.reader_book_list_title
import biblereader.composeapp.generated.resources.reader_next_chapter
import biblereader.composeapp.generated.resources.reader_prev_chapter
import domain.model.BibleBook
import domain.model.BibleVerse
import domain.model.CanonicalBooks
import domain.model.enums.Language
import domain.model.enums.Translation
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** Bible reader screen. Zero business logic — dispatches Intents only. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReaderContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderContent(
    uiState: ReaderUiState,
    onIntent: (ReaderIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            ReaderTopBar(
                bookName = uiState.bookName,
                chapter = uiState.chapter,
                translation = uiState.translation,
                onOpenBookList = { onIntent(ReaderIntent.OpenBookList) },
                onSwitchTranslation = { onIntent(ReaderIntent.SwitchTranslation(it)) },
            )
        },
        bottomBar = {
            ReaderNavBar(
                onPrevious = { onIntent(ReaderIntent.NavigateToPreviousChapter) },
                onNext = { onIntent(ReaderIntent.NavigateToNextChapter) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                uiState.error != null -> Text(
                    text = uiState.error ?: "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.error,
                )
                else -> VerseList(
                    verses = uiState.verses,
                    bookName = uiState.bookName,
                    selectedVerseNumber = uiState.selectedVerseNumber,
                    language = uiState.language,
                    onSelectVerse = { onIntent(ReaderIntent.SelectVerse(it)) },
                    onClearSelection = { onIntent(ReaderIntent.ClearVerseSelection) },
                )
            }
            if (uiState.showBookList) {
                BookListSheet(
                    books = uiState.bookList,
                    language = uiState.language,
                    onSelectBook = { bookId ->
                        onIntent(ReaderIntent.NavigateToChapter(bookId, 1))
                        onIntent(ReaderIntent.DismissBookList)
                    },
                    onDismiss = { onIntent(ReaderIntent.DismissBookList) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun ReaderContentLoadingPreview() {
    MaterialTheme {
        ReaderContent(
            uiState = ReaderUiState(isLoading = true),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ReaderContentPopulatedPreview() {
    MaterialTheme {
        ReaderContent(
            uiState = previewUiState(),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ReaderContentSelectedVersePreview() {
    MaterialTheme {
        ReaderContent(
            uiState = previewUiState().copy(selectedVerseNumber = 1),
            onIntent = {},
        )
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderTopBar(
    bookName: String,
    chapter: Int,
    translation: Translation,
    onOpenBookList: () -> Unit,
    onSwitchTranslation: (Translation) -> Unit,
) {
    TopAppBar(
        title = {
            TextButton(onClick = onOpenBookList) {
                Text(
                    text = if (bookName.isNotEmpty()) "$bookName $chapter"
                    else stringResource(Res.string.reader_book_list_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        actions = {
            TranslationToggle(current = translation, onSwitch = onSwitchTranslation)
        },
    )
}

@Preview
@Composable
private fun ReaderTopBarPreview() {
    MaterialTheme {
        ReaderTopBar(
            bookName = "Genèse", chapter = 1,
            translation = Translation.LOUIS_SEGOND,
            onOpenBookList = {}, onSwitchTranslation = {},
        )
    }
}

@Composable
private fun TranslationToggle(
    current: Translation,
    onSwitch: (Translation) -> Unit,
) {
    val next = if (current == Translation.LOUIS_SEGOND) Translation.KJV else Translation.LOUIS_SEGOND
    val label = if (current == Translation.LOUIS_SEGOND) "LSG" else "KJV"
    TextButton(onClick = { onSwitch(next) }) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview
@Composable
private fun TranslationTogglePreview() {
    MaterialTheme { TranslationToggle(current = Translation.LOUIS_SEGOND, onSwitch = {}) }
}

@Composable
private fun VerseList(
    verses: List<BibleVerse>,
    bookName: String,
    selectedVerseNumber: Int?,
    language: Language,
    onSelectVerse: (Int) -> Unit,
    onClearSelection: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        items(verses, key = { it.verse }) { verse ->
            VerseRow(
                verse = verse,
                bookName = bookName,
                isSelected = verse.verse == selectedVerseNumber,
                onSelect = {
                    if (verse.verse == selectedVerseNumber) onClearSelection()
                    else onSelectVerse(verse.verse)
                },
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun VerseRow(
    verse: BibleVerse,
    bookName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
            )
            .padding(vertical = 6.dp),
    ) {
        if (isSelected) {
            // AC-A-3: show verse reference label when selected
            Text(
                text = "$bookName ${verse.chapter}:${verse.verse}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("${verse.verse} ") }
                append(verse.text)
            },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun VerseRowPreview() {
    MaterialTheme {
        Column {
            VerseRow(verse = previewVerse(1), bookName = "Genèse", isSelected = false, onSelect = {})
            VerseRow(verse = previewVerse(2), bookName = "Genèse", isSelected = true, onSelect = {})
        }
    }
}

@Composable
private fun ReaderNavBar(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        OutlinedButton(onClick = onPrevious) {
            Text(stringResource(Res.string.reader_prev_chapter))
        }
        Button(onClick = onNext) {
            Text(stringResource(Res.string.reader_next_chapter))
        }
    }
}

@Preview
@Composable
private fun ReaderNavBarPreview() {
    MaterialTheme { ReaderNavBar(onPrevious = {}, onNext = {}) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookListSheet(
    books: List<BibleBook>,
    language: Language,
    onSelectBook: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Text(
            text = stringResource(Res.string.reader_book_list_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
        )
        HorizontalDivider()
        LazyColumn {
            items(books, key = { it.bookId }) { book ->
                BookListRow(
                    name = if (language == Language.FR) book.nameFr else book.nameEn,
                    onClick = { onSelectBook(book.bookId) },
                )
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun BookListRow(name: String, onClick: () -> Unit) {
    Column {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
        HorizontalDivider()
    }
}

@Preview
@Composable
private fun BookListRowPreview() {
    MaterialTheme { BookListRow(name = "Genèse", onClick = {}) }
}

// ── Preview helpers ───────────────────────────────────────────────────────────

private fun previewVerse(number: Int) = BibleVerse(
    translation = "LSG", bookId = "GEN", chapter = 1, verse = number,
    text = "Au commencement, Dieu créa les cieux et la terre.",
)

private fun previewUiState() = ReaderUiState(
    translation = Translation.LOUIS_SEGOND,
    language = Language.FR,
    bookId = "GEN",
    chapter = 1,
    bookName = "Genèse",
    totalChapters = 50,
    verses = listOf(
        previewVerse(1),
        previewVerse(2),
        previewVerse(3),
    ),
    bookList = CanonicalBooks.all,
)