package domain.model

import domain.model.enums.Translation
import kotlinx.datetime.Instant

/**
 * A saved verse or verse range with optional user note.
 * [deletedAt] is used for sync propagation only — hard-delete is required during account deletion (AC-E-9).
 */
data class Bookmark(
    val bookmarkId: String,
    val uid: String,
    val translation: Translation,
    val bookId: String,
    val chapterStart: Int,
    val verseStart: Int,
    val chapterEnd: Int,
    val verseEnd: Int,
    val text: String,
    val note: String?,
    val savedAt: Instant,
    val deletedAt: Instant?,
)
