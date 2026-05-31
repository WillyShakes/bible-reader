package feature.onboarding

import domain.model.enums.Language
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import domain.model.enums.Translation
import kotlinx.datetime.LocalDate

/** All user actions in the onboarding flow. One entry per distinct action (RULES.md §MVI). */
sealed interface OnboardingIntent {
    data class SelectLanguage(val language: Language) : OnboardingIntent
    data object ContinueFromWelcome : OnboardingIntent
    data object ContinueFromGracePhilosophy : OnboardingIntent
    data class SelectPlanType(val planType: PlanType) : OnboardingIntent
    data object SelectFreeReading : OnboardingIntent
    data class SelectReadingOrder(val order: ReadingOrder) : OnboardingIntent
    data class SelectStartDate(val date: LocalDate) : OnboardingIntent
    data class SelectTranslation(val translation: Translation) : OnboardingIntent
    data object SignInWithApple : OnboardingIntent
    data object SignInWithGoogle : OnboardingIntent
    data object ContinueWithoutAccount : OnboardingIntent
    data object EnableNotifications : OnboardingIntent
    data object SkipNotifications : OnboardingIntent
    data object CompleteOnboarding : OnboardingIntent
}
