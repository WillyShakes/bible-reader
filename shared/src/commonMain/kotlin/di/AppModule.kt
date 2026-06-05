package di

import app.rema.bible.database.BibleReaderDatabase
import data.local.DatabaseDriverFactory
import data.repository.BibleRepositoryImpl
import domain.repository.BibleRepository
import domain.usecase.GetActivePlanUseCase
import domain.usecase.GetBibleBookUseCase
import domain.usecase.GetBibleChapterUseCase
import domain.usecase.GetBookmarksUseCase
import domain.usecase.RecalculateScheduleUseCase
import domain.usecase.SaveDayCompleteUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetBibleChapterUseCase(get()) }
    factory { GetBibleBookUseCase() }
    factory { RecalculateScheduleUseCase(get()) }
    factory { GetBookmarksUseCase(get()) }
    factory { SaveDayCompleteUseCase(get()) }
    factory { GetActivePlanUseCase(get()) }
}

val dataModule = module {
    single { get<DatabaseDriverFactory>().createDriver() }
    single { BibleReaderDatabase(get()) }
    single<BibleRepository> { BibleRepositoryImpl(get()) }

    // Remaining repository bindings added in their respective feature sessions:
    // single<UserPlanRepository> { UserPlanRepositoryImpl(get(), get()) }
    // single<BookmarkRepository> { BookmarkRepositoryImpl(get(), get()) }
    // single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}