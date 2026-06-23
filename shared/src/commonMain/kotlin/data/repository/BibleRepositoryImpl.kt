package data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.database.BibleVerse as BibleVerseRow
import app.rema.bible.database.BibleReaderDatabase
import domain.model.BibleVerse
import domain.model.enums.Translation
import domain.repository.BibleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * SQLDelight-backed implementation of [BibleRepository].
 * All queries are served from the bundled local database — no network calls (AC-A-1, AC-A-2).
 */
class BibleRepositoryImpl(private val db: BibleReaderDatabase) : BibleRepository {

    override fun observeChapter(
        translation: Translation,
        bookId: String,
        chapter: Int,
    ): Flow<List<BibleVerse>> =
        db.bibleVerseQueries
            .getChapter(translation.dbKey, bookId, chapter.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getVerse(
        translation: Translation,
        bookId: String,
        chapter: Int,
        verse: Int,
    ): BibleVerse? =
        db.bibleVerseQueries
            .getVerse(translation.dbKey, bookId, chapter.toLong(), verse.toLong())
            .executeAsOneOrNull()
            ?.toDomain()
}

/** Maps the SQLDelight generated row type to the domain [BibleVerse]. */
private fun BibleVerseRow.toDomain() = BibleVerse(
    translation = translation,
    bookId = book_id,
    chapter = chapter.toInt(),
    verse = verse.toInt(),
    text = text,
)

/**
 * The string key stored in the `translation` column of [BibleVerseRow].
 * Must match the values in the bundled database asset.
 */
private val Translation.dbKey: String
    get() = when (this) {
        Translation.KJV -> "KJV"
        Translation.LOUIS_SEGOND -> "LSG"
    }