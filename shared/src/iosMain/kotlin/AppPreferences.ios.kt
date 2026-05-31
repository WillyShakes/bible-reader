package app.rema.bible.shared

import platform.Foundation.NSUserDefaults

/** iOS implementation backed by NSUserDefaults. */
actual object AppPreferences {
    actual fun hasCompletedOnboarding(): Boolean =
        NSUserDefaults.standardUserDefaults.boolForKey("has_completed_onboarding")

    actual fun setOnboardingCompleted() {
        NSUserDefaults.standardUserDefaults.setBool(true, forKey = "has_completed_onboarding")
    }
}
