package app.rema.bible.shared

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
}
