package feature.onboarding

import androidx.lifecycle.ViewModel
import app.rema.bible.shared.AppPreferences
import domain.model.enums.Language
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import domain.model.enums.Translation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Drives the onboarding flow (Steps 1–8, SPEC.md §g). */
class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(run {
        // AC-G-1: pre-select language from device locale on first install.
        // getDeviceLocale() returns FR when AppPreferences is not yet initialised (JVM tests),
        // so existing tests that assert FR at init remain stable.
        val initialLang = AppPreferences.getDeviceLocale()
        OnboardingUiState(
            selectedLanguage = initialLang,
            selectedTranslation = if (initialLang == Language.FR) Translation.LOUIS_SEGOND else Translation.KJV,
        )
    })
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /** Routes user actions to the appropriate state update. */
    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SelectLanguage -> selectLanguage(intent.language)
            is OnboardingIntent.ContinueFromWelcome -> _uiState.update { it.copy(currentStep = 2) }
            // Step 2 — non-skippable by design
            is OnboardingIntent.ContinueFromGracePhilosophy -> _uiState.update { it.copy(currentStep = 3) }
            // Step 3 — branches into 4a or 4b
            is OnboardingIntent.SelectPlanType -> handleSelectPlanType(intent.planType)
            is OnboardingIntent.SelectFreeReading -> handleSelectFreeReading()
            // Step 4a
            is OnboardingIntent.SelectReadingOrder,
            is OnboardingIntent.SelectStartDate,
            is OnboardingIntent.ConfirmPlanConfig,
            is OnboardingIntent.OpenMidPlanEntry -> handleStep4a(intent)
            // Step 4b
            is OnboardingIntent.ConfirmFreeReading -> _uiState.update { it.copy(currentStep = 5) }
            // Step 5
            is OnboardingIntent.SelectTranslation -> selectTranslation(intent.translation)
            is OnboardingIntent.ConfirmTranslation -> _uiState.update { it.copy(currentStep = 6) }
            // Step 6
            is OnboardingIntent.SignInWithApple,
            is OnboardingIntent.SignInWithGoogle,
            is OnboardingIntent.SignInWithEmail,
            is OnboardingIntent.ContinueWithoutAccount -> handleStep6Auth()
            // Step 7
            is OnboardingIntent.SetNotificationTime ->
                _uiState.update { it.copy(notificationTime = intent.time) }
            is OnboardingIntent.EnableNotifications,
            is OnboardingIntent.NotificationPermissionDenied,
            is OnboardingIntent.SkipNotifications -> handleStep7Notification(intent)
            // Step 8
            is OnboardingIntent.CompleteOnboarding -> completeOnboarding()
            is OnboardingIntent.DismissCoachMark -> _uiState.update { it.copy(showCoachMark = false) }
        }
    }

    private fun handleSelectPlanType(planType: PlanType) =
        _uiState.update { it.copy(selectedPlanType = planType, isFreeReadingMode = false, currentStep = 4) }

    private fun handleSelectFreeReading() =
        _uiState.update { it.copy(isFreeReadingMode = true, selectedPlanType = null, currentStep = 4) }

    private fun handleStep4a(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SelectReadingOrder ->
                _uiState.update { it.copy(selectedReadingOrder = intent.order) }
            is OnboardingIntent.SelectStartDate ->
                _uiState.update { it.copy(startDate = intent.date) }
            is OnboardingIntent.ConfirmPlanConfig -> advanceFromPlanConfig()
            is OnboardingIntent.OpenMidPlanEntry -> Unit // TODO (Feature b): AC-B-10 mid-plan entry sub-flow
            else -> Unit
        }
    }

    private fun handleStep6Auth() {
        // TODO (Feature e): wire Firebase Auth — for now advance to Step 7
        _uiState.update { it.copy(currentStep = 7) }
    }

    private fun handleStep7Notification(intent: OnboardingIntent) {
        when (intent) {
            // OS permission prompt is triggered by the Screen, not here.
            // ViewModel advances only after the Screen reports the result via NotificationPermissionDenied.
            is OnboardingIntent.EnableNotifications -> Unit
            is OnboardingIntent.NotificationPermissionDenied ->
                _uiState.update { it.copy(notificationPermissionDenied = true, currentStep = 8) }
            is OnboardingIntent.SkipNotifications -> _uiState.update { it.copy(currentStep = 8) }
            else -> Unit
        }
    }

    private fun selectLanguage(language: Language) {
        // Default translation to match language preference (AC-G-5: pre-select matching translation).
        val defaultTranslation = if (language == Language.FR) Translation.LOUIS_SEGOND else Translation.KJV
        _uiState.update { it.copy(selectedLanguage = language, selectedTranslation = defaultTranslation) }
        AppPreferences.setPreferredLanguage(language)
    }

    private fun selectTranslation(translation: Translation) {
        _uiState.update { it.copy(selectedTranslation = translation) }
    }

    private fun advanceFromPlanConfig() {
        // startDate defaults to today if not explicitly set — the date picker defaults to today per spec.
        _uiState.update { it.copy(currentStep = 5) }
    }

    private fun completeOnboarding() {
        // Called at AC-G-8 transition. setOnboardingCompleted() is called EXACTLY HERE and nowhere else.
        // TODO (Feature b / BACKLOG): create UserPlan from selectedPlanType + selectedReadingOrder + startDate.
        _uiState.update { it.copy(showCoachMark = !it.isFreeReadingMode) }
        AppPreferences.setOnboardingCompleted()
    }
}