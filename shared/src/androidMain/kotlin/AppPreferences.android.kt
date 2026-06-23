package app.rema.bible.shared

import android.content.SharedPreferences
import domain.model.enums.Language
import java.util.Locale

/**
 * Android implementation backed by SharedPreferences.
 * [prefs] is initialized in AndroidApplication.onCreate() before Koin starts,
 * via applicationContext injected as a Koin singleton.
 */
actual object AppPreferences {
    private var prefs: SharedPreferences? = null

    fun init(sharedPreferences: SharedPreferences) {
        prefs = sharedPreferences
    }

    actual fun hasCompletedOnboarding(): Boolean =
        prefs?.getBoolean("has_completed_onboarding", false) ?: false

    actual fun setOnboardingCompleted() {
        prefs?.edit()?.putBoolean("has_completed_onboarding", true)?.apply()
    }

    actual fun getPreferredLanguage(): Language {
        val code = prefs?.getString("preferred_language", Language.FR.name) ?: Language.FR.name
        return Language.entries.firstOrNull { it.name == code } ?: Language.FR
    }

    actual fun setPreferredLanguage(language: Language) {
        prefs?.edit()?.putString("preferred_language", language.name)?.apply()
    }

    actual fun getDeviceLocale(): Language {
        // When prefs is null (JVM unit tests, no Application), return FR as the safe default
        // so tests that predate locale detection do not break.
        if (prefs == null) return Language.FR
        val tag = Locale.getDefault().language
        return when {
            tag.startsWith("fr") -> Language.FR
            tag.startsWith("en") -> Language.EN
            else -> Language.FR
        }
    }
}
