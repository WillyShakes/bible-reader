package di

import feature.bookmarks.BookmarksViewModel
import feature.catchup.CatchUpViewModel
import feature.notifications.NotificationsViewModel
import feature.onboarding.OnboardingViewModel
import feature.plan.PlanViewModel
import feature.reader.ReaderViewModel
import feature.sync.SyncViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { ReaderViewModel(get(), get()) }
    viewModel { PlanViewModel(get(), get(), get()) }
    viewModel { CatchUpViewModel(get()) }
    viewModel { BookmarksViewModel(get(), get()) }
    viewModel { OnboardingViewModel() }
    viewModel { SyncViewModel(get()) }
    viewModel { NotificationsViewModel() }
}
