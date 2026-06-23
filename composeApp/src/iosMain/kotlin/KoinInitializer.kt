import data.local.DatabaseDriverFactory
import di.dataModule
import di.domainModule
import di.presentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Called from the Swift @main entry point before the Compose window is presented.
 * Registers the iOS-specific [DatabaseDriverFactory] alongside the shared modules.
 */
fun initKoin() {
    startKoin {
        modules(
            module { single { DatabaseDriverFactory() } },
            domainModule,
            dataModule,
            presentationModule,
        )
    }
}