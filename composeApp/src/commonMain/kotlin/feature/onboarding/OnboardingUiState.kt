package feature.onboarding

import domain.model.enums.Language
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import domain.model.enums.Translation

/** Single source of truth for the onboarding flow (Steps 1–8, SPEC.md §g). */
data class OnboardingUiState(
    val currentStep: Int = 1,
    val selectedLanguage: Language = Language.FR,
    val selectedPlanType: PlanType? = null,
    val selectedReadingOrder: ReadingOrder? = null,
    val selectedTranslation: Translation? = null,
    val isFreeReadingMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)
