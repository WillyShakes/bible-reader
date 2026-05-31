package app.rema.bible.shared

import android.content.SharedPreferences

/**
 * Android implementation backed by SharedPreferences.
 * [prefs] is initialized in AndroidApplication.onCreate() before Koin starts,
 * via applicationContext injected as a Koin singleton.
 */
actual object AppPreferences {
    private lateinit var prefs: SharedPreferences

    fun init(sharedPreferences: SharedPreferences) {
        prefs = sharedPreferences
    }

    actual fun hasCompletedOnboarding(): Boolean =
        prefs.getBoolean("has_completed_onboarding", false)

    actual fun setOnboardingCompleted() {
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
    }
}
