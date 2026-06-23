package data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.rema.bible.database.BibleReaderDatabase
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSUserDomainMask

private const val DB_NAME = "bible_reader.db"

/** iOS actual: copies the bundled resource to the Application Support directory on first install. */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        copyAssetIfNeeded()
        return NativeSqliteDriver(
            schema = BibleReaderDatabase.Schema,
            name = DB_NAME,
        )
    }

    private fun copyAssetIfNeeded() {
        val fm = NSFileManager.defaultManager
        val dirs = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory, NSUserDomainMask, true
        )
        val appSupportDir = dirs.firstOrNull() as? String ?: return
        val destPath = "$appSupportDir/$DB_NAME"
        if (fm.fileExistsAtPath(destPath)) return
        val bundlePath = NSBundle.mainBundle.pathForResource("bible_reader", ofType = "db") ?: return
        fm.copyItemAtPath(bundlePath, toPath = destPath, error = null)
    }
}