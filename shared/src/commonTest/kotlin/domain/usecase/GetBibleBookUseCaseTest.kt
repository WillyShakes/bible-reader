package domain.usecase

import domain.model.CanonicalBooks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetBibleBookUseCaseTest {

    private val useCase = GetBibleBookUseCase()

    @Test
    fun `invoke returns all 66 books in canonical order`() {
        val books = useCase()
        assertEquals(66, books.size)
        assertEquals(CanonicalBooks.all, books)
    }

    @Test
    fun `byId returns the correct book`() {
        val john = useCase.byId("JHN")
        assertNotNull(john)
        assertEquals("Jean", john.nameFr)
        assertEquals("John", john.nameEn)
        assertEquals(21, john.totalChapters)
    }

    @Test
    fun `byId returns null for unknown bookId`() {
        assertNull(useCase.byId("XYZ"))
    }

    @Test
    fun `invoke result is stable across multiple calls`() {
        assertEquals(useCase(), useCase())
    }
}