package data.local

import app.cash.sqldelight.db.SqlDriver

/**
 * Creates the platform-specific SQLDelight driver for [app.rema.bible.database.BibleReaderDatabase].
 *
 * Both platform actuals copy the pre-populated `bible_reader.db` asset to the app's database
 * directory on first install, then open the driver against that file. This gives us
 * zero-migration-time access to all ~62,000 Bible verses (AC-A-1, AC-A-2).
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}