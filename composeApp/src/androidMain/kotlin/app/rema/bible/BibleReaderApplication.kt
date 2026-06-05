package app.rema.bible

import android.app.Application
import app.rema.bible.shared.AppPreferences
import data.local.DatabaseDriverFactory
import di.dataModule
import di.domainModule
import di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BibleReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize AppPreferences before Koin so it is available on first cold start.
        AppPreferences.init(getSharedPreferences("app_prefs", MODE_PRIVATE))

        startKoin {
            androidContext(this@BibleReaderApplication)
            modules(
                module { single { DatabaseDriverFactory(androidContext()) } },
                domainModule,
                dataModule,
                presentationModule,
            )
        }
    }
}
