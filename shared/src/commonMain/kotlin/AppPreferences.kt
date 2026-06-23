package app.rema.bible.shared

import domain.model.enums.Language

/**
 * Device-local preferences that are never synced to Firestore.
 * Stored in platform-native preference storage (SharedPreferences / NSUserDefaults).
 */
expect object AppPreferences {
    /** Returns true if the user has completed onboarding on this device. */
    fun hasCompletedOnboarding(): Boolean

    /**
     * Marks onboarding as complete. Called exactly once at AC-G-8 transition.
     * Never reset by sign-out, account deletion, or plan changes.
     */
    fun setOnboardingCompleted()

    /** Returns the user's preferred language, defaulting to [Language.FR]. */
    fun getPreferredLanguage(): Language

    /** Persists the user's preferred language. Called at AC-G-1 on language selection. */
    fun setPreferredLanguage(language: Language)

    /**
     * Returns the device's system locale mapped to a supported [Language].
     * Falls back to [Language.FR] for any locale not explicitly supported (AC-G-1).
     */
    fun getDeviceLocale(): Language
}
