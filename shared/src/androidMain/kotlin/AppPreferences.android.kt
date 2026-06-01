package app.rema.bible.shared

import android.content.SharedPreferences
import domain.model.enums.Language

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
}
