package domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CanonicalBooksTest {

    @Test
    fun `all returns exactly 66 books`() {
        assertEquals(66, CanonicalBooks.all.size)
    }

    @Test
    fun `all books have unique bookIds`() {
        val ids = CanonicalBooks.all.map { it.bookId }
        assertEquals(ids.distinct().size, ids.size)
    }

    @Test
    fun `canonicalOrder is sequential from 1 to 66`() {
        val orders = CanonicalBooks.all.map { it.canonicalOrder }
        assertEquals((1..66).toList(), orders)
    }

    @Test
    fun `first book is Genesis`() {
        val genesis = CanonicalBooks.all.first()
        assertEquals("GEN", genesis.bookId)
        assertEquals("Genèse", genesis.nameFr)
        assertEquals("Genesis", genesis.nameEn)
        assertEquals(50, genesis.totalChapters)
    }

    @Test
    fun `last book is Revelation`() {
        val revelation = CanonicalBooks.all.last()
        assertEquals("REV", revelation.bookId)
        assertEquals("Apocalypse", revelation.nameFr)
        assertEquals("Revelation", revelation.nameEn)
        assertEquals(22, revelation.totalChapters)
    }

    @Test
    fun `findById returns correct book`() {
        val psalms = CanonicalBooks.findById("PSA")
        assertNotNull(psalms)
        assertEquals(150, psalms.totalChapters)
        assertEquals("Psaumes", psalms.nameFr)
    }

    @Test
    fun `findById returns null for unknown id`() {
        assertNull(CanonicalBooks.findById("UNKNOWN"))
    }

    @Test
    fun `nextBook returns next canonical book`() {
        val genesis = CanonicalBooks.findById("GEN")!!
        val exodus = CanonicalBooks.nextBook(genesis.bookId)
        assertNotNull(exodus)
        assertEquals("EXO", exodus.bookId)
    }

    @Test
    fun `nextBook returns null for Revelation`() {
        assertNull(CanonicalBooks.nextBook("REV"))
    }

    @Test
    fun `previousBook returns previous canonical book`() {
        val exodus = CanonicalBooks.findById("EXO")!!
        val genesis = CanonicalBooks.previousBook(exodus.bookId)
        assertNotNull(genesis)
        assertEquals("GEN", genesis.bookId)
    }

    @Test
    fun `previousBook returns null for Genesis`() {
        assertNull(CanonicalBooks.previousBook("GEN"))
    }

    @Test
    fun `all books have non-blank names in both languages`() {
        CanonicalBooks.all.forEach { book ->
            assert(book.nameFr.isNotBlank()) { "${book.bookId} has blank French name" }
            assert(book.nameEn.isNotBlank()) { "${book.bookId} has blank English name" }
        }
    }

    @Test
    fun `all books have positive chapter counts`() {
        CanonicalBooks.all.forEach { book ->
            assert(book.totalChapters > 0) { "${book.bookId} has zero or negative chapters" }
        }
    }

    @Test
    fun `Psalms has 150 chapters`() {
        assertEquals(150, CanonicalBooks.findById("PSA")?.totalChapters)
    }

    @Test
    fun `Obadiah and Philemon and Jude have 1 chapter`() {
        assertEquals(1, CanonicalBooks.findById("OBA")?.totalChapters)
        assertEquals(1, CanonicalBooks.findById("PHM")?.totalChapters)
        assertEquals(1, CanonicalBooks.findById("JUD")?.totalChapters)
    }
}