package di

import domain.usecase.GetActivePlanUseCase
import domain.usecase.GetBibleChapterUseCase
import domain.usecase.GetBookmarksUseCase
import domain.usecase.RecalculateScheduleUseCase
import domain.usecase.SaveDayCompleteUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetBibleChapterUseCase(get()) }
    factory { RecalculateScheduleUseCase(get()) }
    factory { GetBookmarksUseCase(get()) }
    factory { SaveDayCompleteUseCase(get()) }
    factory { GetActivePlanUseCase(get()) }
}

/**
 * Data module stub — implementations are registered here once Feature a (Bible Content)
 * and Feature e (Sync) are implemented.
 */
val dataModule = module {
    // single<BibleRepository> { BibleRepositoryImpl(get(), get()) }
    // single<UserPlanRepository> { UserPlanRepositoryImpl(get(), get()) }
    // single<BookmarkRepository> { BookmarkRepositoryImpl(get(), get()) }
    // single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}
