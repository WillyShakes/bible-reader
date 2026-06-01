package feature.onboarding

import domain.model.enums.Language
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import domain.model.enums.Translation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** All user actions in the onboarding flow. One entry per distinct action (RULES.md §MVI). */
sealed interface OnboardingIntent {
    // Step 1
    data class SelectLanguage(val language: Language) : OnboardingIntent
    data object ContinueFromWelcome : OnboardingIntent

    // Step 2
    data object ContinueFromGracePhilosophy : OnboardingIntent

    // Step 3
    data class SelectPlanType(val planType: PlanType) : OnboardingIntent
    data object SelectFreeReading : OnboardingIntent

    // Step 4a
    data class SelectReadingOrder(val order: ReadingOrder) : OnboardingIntent
    data class SelectStartDate(val date: LocalDate) : OnboardingIntent
    data object ConfirmPlanConfig : OnboardingIntent
    data object OpenMidPlanEntry : OnboardingIntent

    // Step 4b
    data object ConfirmFreeReading : OnboardingIntent

    // Step 5
    data class SelectTranslation(val translation: Translation) : OnboardingIntent
    data object ConfirmTranslation : OnboardingIntent

    // Step 6
    data object SignInWithApple : OnboardingIntent
    data object SignInWithGoogle : OnboardingIntent
    data object SignInWithEmail : OnboardingIntent
    data object ContinueWithoutAccount : OnboardingIntent

    // Step 7
    data class SetNotificationTime(val time: LocalTime) : OnboardingIntent
    data object EnableNotifications : OnboardingIntent
    data object SkipNotifications : OnboardingIntent
    data object NotificationPermissionDenied : OnboardingIntent

    // Step 8
    data object CompleteOnboarding : OnboardingIntent
    data object DismissCoachMark : OnboardingIntent
}