package app.rema.bible

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.rema.bible.shared.AppPreferences
import feature.onboarding.OnboardingScreen

/**
 * Root composable. Called from MainActivity (Android) and the iOS @main entry point.
 * Routes to Onboarding if hasCompletedOnboarding() is false; otherwise to the home screen.
 */
@Composable
fun App() {
    val navController = rememberNavController()
    val startDestination = if (AppPreferences.hasCompletedOnboarding()) "home" else "onboarding"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
            )
        }
        composable("home") {
            // TODO (Feature b): replace with PlanScreen() / ReaderScreen()
            Text("Home")
        }
    }
}
