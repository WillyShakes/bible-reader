package di

import app.rema.bible.database.BibleReaderDatabase
import data.local.DatabaseDriverFactory
import data.repository.BibleRepositoryImpl
import domain.repository.BibleRepository
import domain.usecase.GetBibleBookUseCase
import domain.usecase.GetBibleChapterUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetBibleChapterUseCase(get()) }
    factory { GetBibleBookUseCase() }

    // Use cases below depend on repositories not yet implemented.
    // Uncomment each when its feature is being built:
    // factory { GetActivePlanUseCase(get()) }          — Feature b
    // factory { SaveDayCompleteUseCase(get()) }        — Feature b
    // factory { RecalculateScheduleUseCase(get()) }    — Feature c
    // factory { GetBookmarksUseCase(get()) }           — Feature f
}

val dataModule = module {
    single { get<DatabaseDriverFactory>().createDriver() }
    single { BibleReaderDatabase(get()) }
    single<BibleRepository> { BibleRepositoryImpl(get()) }

    // Remaining repository bindings added in their respective feature sessions:
    // single<UserPlanRepository> { UserPlanRepositoryImpl(get(), get()) }    — Feature b
    // single<BookmarkRepository> { BookmarkRepositoryImpl(get(), get()) }    — Feature f
    // single<NotificationRepository> { NotificationRepositoryImpl(get()) }  — Feature d
}