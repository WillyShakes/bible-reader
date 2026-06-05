package app.rema.bible.shared

import domain.model.enums.Language
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

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

    actual fun getDeviceLocale(): Language {
        val tag = NSLocale.currentLocale.languageCode
        return when {
            tag.startsWith("fr") -> Language.FR
            tag.startsWith("en") -> Language.EN
            else -> Language.FR
        }
    }
}
