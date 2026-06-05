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
    viewModel { PlanViewModel() }
    viewModel { CatchUpViewModel() }
    viewModel { BookmarksViewModel() }
    viewModel { OnboardingViewModel() }
    viewModel { SyncViewModel() }
    viewModel { NotificationsViewModel() }
}