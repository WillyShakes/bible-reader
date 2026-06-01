package feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import domain.model.enums.Language
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import domain.model.enums.Translation
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import biblereader.composeapp.generated.resources.Res
import biblereader.composeapp.generated.resources.onboarding_account_title
import biblereader.composeapp.generated.resources.onboarding_already_reading
import biblereader.composeapp.generated.resources.onboarding_coach_mark_dismiss
import biblereader.composeapp.generated.resources.onboarding_coach_mark_read
import biblereader.composeapp.generated.resources.onboarding_confirm
import biblereader.composeapp.generated.resources.onboarding_continue
import biblereader.composeapp.generated.resources.onboarding_free_reading_message
import biblereader.composeapp.generated.resources.onboarding_free_reading_preview_label
import biblereader.composeapp.generated.resources.onboarding_free_reading_start
import biblereader.composeapp.generated.resources.onboarding_grace_body
import biblereader.composeapp.generated.resources.onboarding_language_english
import biblereader.composeapp.generated.resources.onboarding_language_français
import biblereader.composeapp.generated.resources.onboarding_mode_free
import biblereader.composeapp.generated.resources.onboarding_mode_free_desc
import biblereader.composeapp.generated.resources.onboarding_mode_plan_1yr
import biblereader.composeapp.generated.resources.onboarding_mode_plan_1yr_desc
import biblereader.composeapp.generated.resources.onboarding_mode_plan_6mo
import biblereader.composeapp.generated.resources.onboarding_mode_plan_6mo_desc
import biblereader.composeapp.generated.resources.onboarding_mode_title
import biblereader.composeapp.generated.resources.onboarding_no_account
import biblereader.composeapp.generated.resources.onboarding_notif_body
import biblereader.composeapp.generated.resources.onboarding_notif_denied
import biblereader.composeapp.generated.resources.onboarding_notif_enable
import biblereader.composeapp.generated.resources.onboarding_notif_skip
import biblereader.composeapp.generated.resources.onboarding_notif_time_label
import biblereader.composeapp.generated.resources.onboarding_notif_title
import biblereader.composeapp.generated.resources.onboarding_order_canon
import biblereader.composeapp.generated.resources.onboarding_order_canon_desc
import biblereader.composeapp.generated.resources.onboarding_order_chrono
import biblereader.composeapp.generated.resources.onboarding_order_chrono_desc
import biblereader.composeapp.generated.resources.onboarding_plan_config_title
import biblereader.composeapp.generated.resources.onboarding_reading_order_label
import biblereader.composeapp.generated.resources.onboarding_sign_in_apple
import biblereader.composeapp.generated.resources.onboarding_sign_in_email
import biblereader.composeapp.generated.resources.onboarding_sign_in_google
import biblereader.composeapp.generated.resources.onboarding_start
import biblereader.composeapp.generated.resources.onboarding_start_date_label
import biblereader.composeapp.generated.resources.onboarding_start_date_today
import biblereader.composeapp.generated.resources.onboarding_translation_kjv
import biblereader.composeapp.generated.resources.onboarding_translation_ls
import biblereader.composeapp.generated.resources.onboarding_translation_preview_kjv
import biblereader.composeapp.generated.resources.onboarding_translation_preview_label
import biblereader.composeapp.generated.resources.onboarding_translation_preview_ls
import biblereader.composeapp.generated.resources.onboarding_translation_title
import biblereader.composeapp.generated.resources.onboarding_welcome_title
import biblereader.composeapp.generated.resources.onboarding_welcome_value_prop

/**
 * Onboarding flow entry point. Routes to the correct step composable based on [OnboardingUiState.currentStep].
 * Zero business logic — dispatches Intents only. (RULES.md §MVI)
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel(),
    onOnboardingComplete: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // AC-G-8: navigate out when step reaches 8 and onboarding is marked complete.
    LaunchedEffect(uiState.currentStep) {
        if (uiState.currentStep == 8) {
            viewModel.onIntent(OnboardingIntent.CompleteOnboarding)
            onOnboardingComplete()
        }
    }

    when (uiState.currentStep) {
        1 -> WelcomeStep(uiState = uiState, onIntent = viewModel::onIntent)
        2 -> GracePhilosophyStep(onIntent = viewModel::onIntent)
        3 -> ReadingModeStep(onIntent = viewModel::onIntent)
        4 -> if (uiState.isFreeReadingMode) {
            FreeReadingStep(onIntent = viewModel::onIntent)
        } else {
            PlanConfigStep(uiState = uiState, onIntent = viewModel::onIntent)
        }
        5 -> TranslationStep(uiState = uiState, onIntent = viewModel::onIntent)
        6 -> AccountStep(onIntent = viewModel::onIntent)
        7 -> NotificationStep(uiState = uiState, onIntent = viewModel::onIntent)
    }
}

// ─── Step 1 ──────────────────────────────────────────────────────────────────

@Composable
private fun WelcomeStep(
    uiState: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(Res.string.onboarding_welcome_title), style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(Res.string.onboarding_welcome_value_prop), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(40.dp))
        LanguageSelectorRow(selectedLanguage = uiState.selectedLanguage, onIntent = onIntent)
        Spacer(Modifier.height(32.dp))
        Button(onClick = { onIntent(OnboardingIntent.ContinueFromWelcome) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.onboarding_continue))
        }
    }
}

@Composable
private fun LanguageSelectorRow(selectedLanguage: Language, onIntent: (OnboardingIntent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LanguageOption(
            label = stringResource(Res.string.onboarding_language_français),
            selected = selectedLanguage == Language.FR,
            onClick = { onIntent(OnboardingIntent.SelectLanguage(Language.FR)) },
            modifier = Modifier.weight(1f),
        )
        LanguageOption(
            label = stringResource(Res.string.onboarding_language_english),
            selected = selectedLanguage == Language.EN,
            onClick = { onIntent(OnboardingIntent.SelectLanguage(Language.EN)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (selected) 4.dp else 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ─── Step 2 ──────────────────────────────────────────────────────────────────

/** Non-skippable by spec (AC-G-2). No Skip button rendered. */
@Composable
private fun GracePhilosophyStep(onIntent: (OnboardingIntent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.onboarding_grace_body),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = { onIntent(OnboardingIntent.ContinueFromGracePhilosophy) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.onboarding_continue))
        }
    }
}

// ─── Step 3 ──────────────────────────────────────────────────────────────────

@Composable
private fun ReadingModeStep(onIntent: (OnboardingIntent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.onboarding_mode_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(32.dp))

        ReadingModeCard(
            title = stringResource(Res.string.onboarding_mode_plan_1yr),
            description = stringResource(Res.string.onboarding_mode_plan_1yr_desc),
            onClick = { onIntent(OnboardingIntent.SelectPlanType(PlanType.ONE_YEAR)) },
        )
        Spacer(Modifier.height(12.dp))
        ReadingModeCard(
            title = stringResource(Res.string.onboarding_mode_plan_6mo),
            description = stringResource(Res.string.onboarding_mode_plan_6mo_desc),
            onClick = { onIntent(OnboardingIntent.SelectPlanType(PlanType.SIX_MONTHS)) },
        )
        Spacer(Modifier.height(12.dp))
        ReadingModeCard(
            title = stringResource(Res.string.onboarding_mode_free),
            description = stringResource(Res.string.onboarding_mode_free_desc),
            onClick = { onIntent(OnboardingIntent.SelectFreeReading) },
        )
    }
}

@Composable
private fun ReadingModeCard(title: String, description: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─── Step 4a ─────────────────────────────────────────────────────────────────

@Composable
private fun PlanConfigStep(
    uiState: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = stringResource(Res.string.onboarding_plan_config_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        ReadingOrderSection(selectedOrder = uiState.selectedReadingOrder, onIntent = onIntent)

        // Start date — label only; date picker is platform-native (expect/actual, Feature g TODO)
        Text(
            text = stringResource(Res.string.onboarding_start_date_label),
            style = MaterialTheme.typography.titleSmall,
        )
        // TODO (Feature g): wire platform date picker via expect/actual
        Text(
            text = uiState.startDate?.toString() ?: stringResource(Res.string.onboarding_start_date_today),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onIntent(OnboardingIntent.ConfirmPlanConfig) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.onboarding_start))
        }
        TextButton(
            onClick = { onIntent(OnboardingIntent.OpenMidPlanEntry) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(stringResource(Res.string.onboarding_already_reading))
        }
    }
}

@Composable
private fun ReadingOrderSection(selectedOrder: ReadingOrder, onIntent: (OnboardingIntent) -> Unit) {
    Column {
        Text(
            text = stringResource(Res.string.onboarding_reading_order_label),
            style = MaterialTheme.typography.titleSmall,
        )
        Column(modifier = Modifier.selectableGroup()) {
            ReadingOrderOption(
                label = stringResource(Res.string.onboarding_order_canon),
                description = stringResource(Res.string.onboarding_order_canon_desc),
                selected = selectedOrder == ReadingOrder.CANON,
                onClick = { onIntent(OnboardingIntent.SelectReadingOrder(ReadingOrder.CANON)) },
            )
            ReadingOrderOption(
                label = stringResource(Res.string.onboarding_order_chrono),
                description = stringResource(Res.string.onboarding_order_chrono_desc),
                selected = selectedOrder == ReadingOrder.CHRONOLOGICAL,
                onClick = { onIntent(OnboardingIntent.SelectReadingOrder(ReadingOrder.CHRONOLOGICAL)) },
            )
        }
    }
}

@Composable
private fun ReadingOrderOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ─── Step 4b ─────────────────────────────────────────────────────────────────

@Composable
private fun FreeReadingStep(onIntent: (OnboardingIntent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.onboarding_free_reading_message),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.onboarding_free_reading_preview_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = { onIntent(OnboardingIntent.ConfirmFreeReading) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.onboarding_free_reading_start))
        }
    }
}

// ─── Step 5 ──────────────────────────────────────────────────────────────────

@Composable
private fun TranslationStep(
    uiState: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = stringResource(Res.string.onboarding_translation_title), style = MaterialTheme.typography.headlineMedium)
        TranslationSelectorSection(selectedTranslation = uiState.selectedTranslation, onIntent = onIntent)
        Button(onClick = { onIntent(OnboardingIntent.ConfirmTranslation) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.onboarding_confirm))
        }
    }
}

@Composable
private fun TranslationSelectorSection(selectedTranslation: Translation, onIntent: (OnboardingIntent) -> Unit) {
    Column(modifier = Modifier.selectableGroup()) {
        TranslationOption(
            label = stringResource(Res.string.onboarding_translation_ls),
            selected = selectedTranslation == Translation.LOUIS_SEGOND,
            onClick = { onIntent(OnboardingIntent.SelectTranslation(Translation.LOUIS_SEGOND)) },
        )
        TranslationOption(
            label = stringResource(Res.string.onboarding_translation_kjv),
            selected = selectedTranslation == Translation.KJV,
            onClick = { onIntent(OnboardingIntent.SelectTranslation(Translation.KJV)) },
        )
    }
    // Hardcoded John 3:16–18 preview — live BibleRepository not available yet.
    // BACKLOG: [Translation] [AC-G-5] — replace hardcoded preview with live BibleRepository query
    Text(text = stringResource(Res.string.onboarding_translation_preview_label), style = MaterialTheme.typography.labelLarge)
    Text(
        text = if (selectedTranslation == Translation.LOUIS_SEGOND) {
            stringResource(Res.string.onboarding_translation_preview_ls)
        } else {
            stringResource(Res.string.onboarding_translation_preview_kjv)
        },
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun TranslationOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

// ─── Step 6 ──────────────────────────────────────────────────────────────────

@Composable
private fun AccountStep(onIntent: (OnboardingIntent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(Res.string.onboarding_account_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        SignInButtons(onIntent = onIntent)
        Spacer(Modifier.height(24.dp))
        // "Continue without account" — peer option, not a lesser fallback (AC-G-6)
        TextButton(onClick = { onIntent(OnboardingIntent.ContinueWithoutAccount) }) {
            Text(stringResource(Res.string.onboarding_no_account))
        }
    }
}

@Composable
private fun SignInButtons(onIntent: (OnboardingIntent) -> Unit) {
    // Apple Sign-In — required on iOS when any social login offered (CLAUDE.md §Architecture Decisions)
    Button(onClick = { onIntent(OnboardingIntent.SignInWithApple) }, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(Res.string.onboarding_sign_in_apple))
    }
    Spacer(Modifier.height(12.dp))
    OutlinedButton(onClick = { onIntent(OnboardingIntent.SignInWithGoogle) }, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(Res.string.onboarding_sign_in_google))
    }
    Spacer(Modifier.height(12.dp))
    OutlinedButton(onClick = { onIntent(OnboardingIntent.SignInWithEmail) }, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(Res.string.onboarding_sign_in_email))
    }
}

// ─── Step 7 ──────────────────────────────────────────────────────────────────

@Composable
private fun NotificationStep(
    uiState: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(Res.string.onboarding_notif_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(Res.string.onboarding_notif_body), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))

        Text(text = stringResource(Res.string.onboarding_notif_time_label), style = MaterialTheme.typography.titleSmall)
        // TODO (Feature g): wire platform time picker via expect/actual for inline time selection
        Text(
            text = "${uiState.notificationTime.hour.toString().padStart(2, '0')}:${uiState.notificationTime.minute.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(32.dp))

        // OS permission prompt fires from the Screen on button tap, not from the ViewModel.
        // Platform-native permission request wired via expect/actual (Feature d).
        NotificationPermissionButton(onIntent = onIntent, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { onIntent(OnboardingIntent.SkipNotifications) }) {
            Text(stringResource(Res.string.onboarding_notif_skip))
        }
        if (uiState.notificationPermissionDenied) {
            Spacer(Modifier.height(16.dp))
            NotificationDeniedMessage()
        }
    }
}

@Composable
private fun NotificationDeniedMessage() {
    Snackbar {
        Text(stringResource(Res.string.onboarding_notif_denied))
    }
}

/**
 * Dispatches [OnboardingIntent.EnableNotifications] on tap.
 * The actual OS permission prompt is wired via expect/actual in Feature d.
 * Until then the ViewModel treats EnableNotifications as a no-op (see BACKLOG).
 */
@Composable
private fun NotificationPermissionButton(
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO (Feature d): replace with expect/actual RequestNotificationPermission composable
    Button(
        onClick = { onIntent(OnboardingIntent.EnableNotifications) },
        modifier = modifier,
    ) {
        Text(stringResource(Res.string.onboarding_notif_enable))
    }
}