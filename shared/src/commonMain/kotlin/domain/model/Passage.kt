package domain.model

/**
 * A contiguous range of verses within a single chapter, or spanning chapters within one book.
 * Null verse boundaries indicate the full chapter.
 */
data class Passage(
    val bookId: String,
    val chapterStart: Int,
    val verseStart: Int?,
    val chapterEnd: Int,
    val verseEnd: Int?,
)
