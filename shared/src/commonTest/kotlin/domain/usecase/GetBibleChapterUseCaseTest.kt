package domain.usecase

import app.cash.turbine.test
import domain.model.BibleVerse
import domain.model.enums.Translation
import domain.repository.BibleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetBibleChapterUseCaseTest {

    private val genesis1Verses = listOf(
        BibleVerse("LSG", "GEN", 1, 1, "Au commencement, Dieu créa les cieux et la terre."),
        BibleVerse("LSG", "GEN", 1, 2, "La terre était informe et vide."),
    )

    private val fakeRepository = object : BibleRepository {
        override fun observeChapter(
            translation: Translation,
            bookId: String,
            chapter: Int,
        ): Flow<List<BibleVerse>> = flowOf(genesis1Verses)

        override suspend fun getVerse(
            translation: Translation,
            bookId: String,
            chapter: Int,
            verse: Int,
        ): BibleVerse? = genesis1Verses.firstOrNull { it.verse == verse }
    }

    private val useCase = GetBibleChapterUseCase(fakeRepository)

    @Test
    fun `invoke delegates to repository and emits verses`() = runTest {
        useCase(Translation.LOUIS_SEGOND, "GEN", 1).test {
            assertEquals(genesis1Verses, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits verses in the order returned by the repository`() = runTest {
        useCase(Translation.LOUIS_SEGOND, "GEN", 1).test {
            val verses = awaitItem()
            assertEquals(1, verses[0].verse)
            assertEquals(2, verses[1].verse)
            awaitComplete()
        }
    }
}