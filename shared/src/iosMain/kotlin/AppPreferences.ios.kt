package app.rema.bible.shared

import domain.model.enums.Language
import platform.Foundation.NSUserDefaults

/** iOS implementation backed by NSUserDefaults. */
actual object AppPreferences {
    actual fun hasCompletedOnboarding(): Boolean =
        NSUserDefaults.standardUserDefaults.boolForKey("has_completed_onboarding")

    actual fun setOnboardingCompleted() {
        NSUserDefaults.standardUserDefaults.setBool(true, forKey = "has_completed_onboarding")
    }

    actual fun getPreferredLanguage(): Language {
        val code = NSUserDefaults.standardUserDefaults.stringForKey("preferred_language")
        return Language.entries.firstOrNull { it.name == code } ?: Language.FR
    }

    actual fun setPreferredLanguage(language: Language) {
        NSUserDefaults.standardUserDefaults.setObject(language.name, forKey = "preferred_language")
    }
}
