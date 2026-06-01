package feature.onboarding

import domain.model.enums.Language
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import domain.model.enums.Translation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel

    @BeforeTest
    fun setup() {
        viewModel = OnboardingViewModel()
    }

    // ─── Step 1 ──────────────────────────────────────────────────────────────

    @Test
    fun `initial state starts at step 1 with French selected`() {
        val state = viewModel.uiState.value
        assertEquals(1, state.currentStep)
        assertEquals(Language.FR, state.selectedLanguage)
    }

    @Test
    fun `SelectLanguage EN updates language and pre-selects KJV`() {
        viewModel.onIntent(OnboardingIntent.SelectLanguage(Language.EN))
        val state = viewModel.uiState.value
        assertEquals(Language.EN, state.selectedLanguage)
        assertEquals(Translation.KJV, state.selectedTranslation)
    }

    @Test
    fun `SelectLanguage FR updates language and pre-selects Louis Segond`() {
        viewModel.onIntent(OnboardingIntent.SelectLanguage(Language.EN))
        viewModel.onIntent(OnboardingIntent.SelectLanguage(Language.FR))
        val state = viewModel.uiState.value
        assertEquals(Language.FR, state.selectedLanguage)
        assertEquals(Translation.LOUIS_SEGOND, state.selectedTranslation)
    }

    @Test
    fun `ContinueFromWelcome advances to step 2`() {
        viewModel.onIntent(OnboardingIntent.ContinueFromWelcome)
        assertEquals(2, viewModel.uiState.value.currentStep)
    }

    // ─── Step 2 ──────────────────────────────────────────────────────────────

    @Test
    fun `ContinueFromGracePhilosophy advances to step 3`() {
        viewModel.onIntent(OnboardingIntent.ContinueFromWelcome)
        viewModel.onIntent(OnboardingIntent.ContinueFromGracePhilosophy)
        assertEquals(3, viewModel.uiState.value.currentStep)
    }

    // ─── Step 3 — branch logic ───────────────────────────────────────────────

    @Test
    fun `SelectPlanType ONE_YEAR advances to step 4 on plan path`() {
        advanceToStep3()
        viewModel.onIntent(OnboardingIntent.SelectPlanType(PlanType.ONE_YEAR))
        val state = viewModel.uiState.value
        assertEquals(4, state.currentStep)
        assertEquals(PlanType.ONE_YEAR, state.selectedPlanType)
        assertFalse(state.isFreeReadingMode)
    }

    @Test
    fun `SelectPlanType SIX_MONTHS advances to step 4 on plan path`() {
        advanceToStep3()
        viewModel.onIntent(OnboardingIntent.SelectPlanType(PlanType.SIX_MONTHS))
        val state = viewModel.uiState.value
        assertEquals(4, state.currentStep)
        assertEquals(PlanType.SIX_MONTHS, state.selectedPlanType)
        assertFalse(state.isFreeReadingMode)
    }

    @Test
    fun `SelectFreeReading advances to step 4 on free reading path`() {
        advanceToStep3()
        viewModel.onIntent(OnboardingIntent.SelectFreeReading)
        val state = viewModel.uiState.value
        assertEquals(4, state.currentStep)
        assertTrue(state.isFreeReadingMode)
        assertNull(state.selectedPlanType)
    }

    // ─── Step 4a ─────────────────────────────────────────────────────────────

    @Test
    fun `SelectReadingOrder CHRONOLOGICAL updates order without advancing step`() {
        advanceToPlanConfig()
        viewModel.onIntent(OnboardingIntent.SelectReadingOrder(ReadingOrder.CHRONOLOGICAL))
        val state = viewModel.uiState.value
        assertEquals(ReadingOrder.CHRONOLOGICAL, state.selectedReadingOrder)
        assertEquals(4, state.currentStep)
    }

    @Test
    fun `SelectStartDate updates date without advancing step`() {
        advanceToPlanConfig()
        val date = LocalDate(2026, 6, 1)
        viewModel.onIntent(OnboardingIntent.SelectStartDate(date))
        val state = viewModel.uiState.value
        assertEquals(date, state.startDate)
        assertEquals(4, state.currentStep)
    }

    @Test
    fun `ConfirmPlanConfig advances to step 5`() {
        advanceToPlanConfig()
        viewModel.onIntent(OnboardingIntent.ConfirmPlanConfig)
        assertEquals(5, viewModel.uiState.value.currentStep)
    }

    // ─── Step 4b ─────────────────────────────────────────────────────────────

    @Test
    fun `ConfirmFreeReading advances to step 5`() {
        advanceToStep3()
        viewModel.onIntent(OnboardingIntent.SelectFreeReading)
        viewModel.onIntent(OnboardingIntent.ConfirmFreeReading)
        assertEquals(5, viewModel.uiState.value.currentStep)
    }

    // ─── Step 5 ──────────────────────────────────────────────────────────────

    @Test
    fun `SelectTranslation KJV updates translation without advancing step`() {
        advanceToPlanConfig()
        viewModel.onIntent(OnboardingIntent.ConfirmPlanConfig)
        viewModel.onIntent(OnboardingIntent.SelectTranslation(Translation.KJV))
        val state = viewModel.uiState.value
        assertEquals(Translation.KJV, state.selectedTranslation)
        assertEquals(5, state.currentStep)
    }

    @Test
    fun `ConfirmTranslation advances to step 6`() {
        advanceToPlanConfig()
        viewModel.onIntent(OnboardingIntent.ConfirmPlanConfig)
        viewModel.onIntent(OnboardingIntent.ConfirmTranslation)
        assertEquals(6, viewModel.uiState.value.currentStep)
    }

    // ─── Step 6 ──────────────────────────────────────────────────────────────

    @Test
    fun `ContinueWithoutAccount advances to step 7`() {
        advanceToStep6()
        viewModel.onIntent(OnboardingIntent.ContinueWithoutAccount)
        assertEquals(7, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `SignInWithApple advances to step 7`() {
        advanceToStep6()
        viewModel.onIntent(OnboardingIntent.SignInWithApple)
        assertEquals(7, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `SignInWithGoogle advances to step 7`() {
        advanceToStep6()
        viewModel.onIntent(OnboardingIntent.SignInWithGoogle)
        assertEquals(7, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `SignInWithEmail advances to step 7`() {
        advanceToStep6()
        viewModel.onIntent(OnboardingIntent.SignInWithEmail)
        assertEquals(7, viewModel.uiState.value.currentStep)
    }

    // ─── Step 7 ──────────────────────────────────────────────────────────────

    @Test
    fun `SetNotificationTime updates time without advancing step`() {
        advanceToStep7()
        val newTime = LocalTime(8, 30)
        viewModel.onIntent(OnboardingIntent.SetNotificationTime(newTime))
        val state = viewModel.uiState.value
        assertEquals(newTime, state.notificationTime)
        assertEquals(7, state.currentStep)
    }

    @Test
    fun `EnableNotifications does not advance step — OS prompt handled by Feature d`() {
        advanceToStep7()
        viewModel.onIntent(OnboardingIntent.EnableNotifications)
        assertEquals(7, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `SkipNotifications advances to step 8`() {
        advanceToStep7()
        viewModel.onIntent(OnboardingIntent.SkipNotifications)
        assertEquals(8, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `NotificationPermissionDenied sets flag and advances to step 8`() {
        advanceToStep7()
        viewModel.onIntent(OnboardingIntent.NotificationPermissionDenied)
        val state = viewModel.uiState.value
        assertEquals(8, state.currentStep)
        assertTrue(state.notificationPermissionDenied)
    }

    // ─── Step 8 — AC-G-8 guard ───────────────────────────────────────────────

    @Test
    fun `CompleteOnboarding sets showCoachMark true for plan users`() {
        advanceThroughAllSteps(freeReading = false)
        viewModel.onIntent(OnboardingIntent.CompleteOnboarding)
        assertTrue(viewModel.uiState.value.showCoachMark)
    }

    @Test
    fun `CompleteOnboarding sets showCoachMark false for free reading users`() {
        advanceThroughAllSteps(freeReading = true)
        viewModel.onIntent(OnboardingIntent.CompleteOnboarding)
        assertFalse(viewModel.uiState.value.showCoachMark)
    }

    @Test
    fun `DismissCoachMark sets showCoachMark to false`() {
        advanceThroughAllSteps(freeReading = false)
        viewModel.onIntent(OnboardingIntent.CompleteOnboarding)
        viewModel.onIntent(OnboardingIntent.DismissCoachMark)
        assertFalse(viewModel.uiState.value.showCoachMark)
    }

    // ─── Default translation pre-selection (AC-G-5) ──────────────────────────

    @Test
    fun `default translation is LOUIS_SEGOND when language is FR`() {
        assertEquals(Translation.LOUIS_SEGOND, viewModel.uiState.value.selectedTranslation)
    }

    @Test
    fun `default notification time is 07 00`() {
        val time = viewModel.uiState.value.notificationTime
        assertEquals(7, time.hour)
        assertEquals(0, time.minute)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun advanceToStep3() {
        viewModel.onIntent(OnboardingIntent.ContinueFromWelcome)
        viewModel.onIntent(OnboardingIntent.ContinueFromGracePhilosophy)
    }

    private fun advanceToPlanConfig() {
        advanceToStep3()
        viewModel.onIntent(OnboardingIntent.SelectPlanType(PlanType.ONE_YEAR))
    }

    private fun advanceToStep6() {
        advanceToPlanConfig()
        viewModel.onIntent(OnboardingIntent.ConfirmPlanConfig)
        viewModel.onIntent(OnboardingIntent.ConfirmTranslation)
    }

    private fun advanceToStep7() {
        advanceToStep6()
        viewModel.onIntent(OnboardingIntent.ContinueWithoutAccount)
    }

    private fun advanceThroughAllSteps(freeReading: Boolean) {
        viewModel.onIntent(OnboardingIntent.ContinueFromWelcome)
        viewModel.onIntent(OnboardingIntent.ContinueFromGracePhilosophy)
        if (freeReading) {
            viewModel.onIntent(OnboardingIntent.SelectFreeReading)
            viewModel.onIntent(OnboardingIntent.ConfirmFreeReading)
        } else {
            viewModel.onIntent(OnboardingIntent.SelectPlanType(PlanType.ONE_YEAR))
            viewModel.onIntent(OnboardingIntent.ConfirmPlanConfig)
        }
        viewModel.onIntent(OnboardingIntent.ConfirmTranslation)
        viewModel.onIntent(OnboardingIntent.ContinueWithoutAccount)
        viewModel.onIntent(OnboardingIntent.SkipNotifications)
    }
}