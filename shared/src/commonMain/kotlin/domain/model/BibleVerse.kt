package domain.model

/** A single verse of Bible text as returned by the domain layer. */
data class BibleVerse(
    val translation: String,
    val bookId: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
)
