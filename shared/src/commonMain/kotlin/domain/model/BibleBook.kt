package domain.model

/**
 * Represents one of the 66 canonical Bible books.
 * This is a static bundled asset — it is never stored in SQLDelight or Firestore.
 *
 * @param bookId       Stable string identifier (e.g. "GEN", "MAT") used as the SQLDelight foreign key.
 * @param nameFr       French book name displayed when app language is [enums.Language.FR] (AC-A-5).
 * @param nameEn       English book name displayed when app language is [enums.Language.EN].
 * @param totalChapters Number of chapters in this book — used by next/previous chapter navigation (AC-A-4).
 * @param canonicalOrder 1-based position in the Protestant canon (Genesis = 1, Revelation = 66).
 */
data class BibleBook(
    val bookId: String,
    val nameFr: String,
    val nameEn: String,
    val totalChapters: Int,
    val canonicalOrder: Int,
)