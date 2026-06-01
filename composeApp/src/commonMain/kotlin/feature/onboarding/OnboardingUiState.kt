package feature.onboarding

import domain.model.enums.Language
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import domain.model.enums.Translation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** Single source of truth for the onboarding flow (Steps 1–8, SPEC.md §g). */
data class OnboardingUiState(
    val currentStep: Int = 1,
    val selectedLanguage: Language = Language.FR,
    val selectedPlanType: PlanType? = null,
    val selectedReadingOrder: ReadingOrder = ReadingOrder.CANON,
    val startDate: LocalDate? = null,
    val selectedTranslation: Translation = Translation.LOUIS_SEGOND,
    val isFreeReadingMode: Boolean = false,
    val notificationTime: LocalTime = LocalTime(7, 0),
    val notificationPermissionDenied: Boolean = false,
    val showCoachMark: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)