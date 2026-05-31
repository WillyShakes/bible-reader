package feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.rema.bible.shared.AppPreferences
import domain.usecase.GetActivePlanUseCase
import domain.usecase.SaveDayCompleteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Drives the onboarding flow (Steps 1–8, SPEC.md §g). */
class OnboardingViewModel(
    private val getActivePlanUseCase: GetActivePlanUseCase,
    private val saveDayCompleteUseCase: SaveDayCompleteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SelectLanguage ->
                _uiState.update { it.copy(selectedLanguage = intent.language) }
            is OnboardingIntent.ContinueFromWelcome ->
                _uiState.update { it.copy(currentStep = 2) }
            is OnboardingIntent.ContinueFromGracePhilosophy ->
                _uiState.update { it.copy(currentStep = 3) }
            is OnboardingIntent.SelectPlanType ->
                _uiState.update { it.copy(selectedPlanType = intent.planType, currentStep = 4, isFreeReadingMode = false) }
            is OnboardingIntent.SelectFreeReading ->
                _uiState.update { it.copy(isFreeReadingMode = true, currentStep = 4) }
            is OnboardingIntent.SelectReadingOrder ->
                _uiState.update { it.copy(selectedReadingOrder = intent.order) }
            is OnboardingIntent.SelectStartDate ->
                _uiState.update { it.copy(currentStep = 5) }
            is OnboardingIntent.SelectTranslation ->
                _uiState.update { it.copy(selectedTranslation = intent.translation) }
            is OnboardingIntent.SignInWithApple,
            is OnboardingIntent.SignInWithGoogle ->
                _uiState.update { it.copy(currentStep = 7) }
            is OnboardingIntent.ContinueWithoutAccount ->
                _uiState.update { it.copy(currentStep = 7) }
            is OnboardingIntent.EnableNotifications,
            is OnboardingIntent.SkipNotifications ->
                _uiState.update { it.copy(currentStep = 8) }
            is OnboardingIntent.CompleteOnboarding ->
                completeOnboarding()
        }
    }

    private fun completeOnboarding() {
        // Called at AC-G-8 transition — the only place setOnboardingCompleted() is ever called.
        AppPreferences.setOnboardingCompleted()
    }
}
