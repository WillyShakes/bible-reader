package feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * Onboarding flow entry point. Renders the correct step based on UiState.currentStep.
 * Zero business logic — dispatches Intents only.
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel(),
    onOnboardingComplete: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // TODO (Feature g): render step screens based on uiState.currentStep
    // Step 1: WelcomeStep, Step 2: GracePhilosophyStep, Step 3: ReadingModeStep,
    // Step 4a: PlanConfigStep, Step 4b: FreeReadingStep, Step 5: TranslationStep,
    // Step 6: AccountStep, Step 7: NotificationStep, Step 8 → onOnboardingComplete()
}
