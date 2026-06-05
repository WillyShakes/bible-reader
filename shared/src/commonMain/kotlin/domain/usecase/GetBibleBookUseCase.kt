package domain.usecase

import domain.model.BibleBook
import domain.model.CanonicalBooks

/**
 * Returns the ordered list of all 66 canonical books, or a single book by ID.
 * Served from the in-memory [CanonicalBooks] asset — no database or network call.
 */
class GetBibleBookUseCase {
    /** Returns all 66 books in canonical order. */
    operator fun invoke(): List<BibleBook> = CanonicalBooks.all

    /** Returns the book with the given [bookId], or null if not found. */
    fun byId(bookId: String): BibleBook? = CanonicalBooks.findById(bookId)
}