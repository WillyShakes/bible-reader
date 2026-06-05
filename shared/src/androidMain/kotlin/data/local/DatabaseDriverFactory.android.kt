package data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.rema.bible.database.BibleReaderDatabase
import java.io.File
import java.io.FileOutputStream

private const val DB_NAME = "bible_reader.db"

/** Android actual: copies the bundled asset to the databases directory on first install. */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        copyAssetIfNeeded()
        return AndroidSqliteDriver(
            schema = BibleReaderDatabase.Schema,
            context = context,
            name = DB_NAME,
        )
    }

    private fun copyAssetIfNeeded() {
        val dbFile = context.getDatabasePath(DB_NAME)
        if (dbFile.exists()) return
        dbFile.parentFile?.mkdirs()
        context.assets.open(DB_NAME).use { input ->
            FileOutputStream(dbFile).use { output -> input.copyTo(output) }
        }
    }
}