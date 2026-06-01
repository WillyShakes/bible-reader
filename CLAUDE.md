# CLAUDE.md — Bible Reader
# This file is read automatically by Claude Code at the start of every session.
# Source of truth: SPEC.md v1.2 (2026-05-30)
# Update this file whenever an architectural decision changes. Bump the version below.
# Current version: 1.0

---

## Project Identity

**Name:** Bible Reader
**Type:** Mobile app (iOS + Android)
**Stack:** Kotlin Multiplatform + Compose Multiplatform + Firebase
**Status:** Alpha — Phase 3 (Architecture scaffold)
**Platforms:** iOS 16+ and Android 10+ (API 29+), simultaneous launch
**Language:** French-first. All copy written in French first, then translated to English.

---

## You Are The Engineer. I Am The Architect.

- You implement. I decide direction.
- When in doubt about scope: ask, don't assume.
- When stuck: say so immediately. Do not hallucinate a workaround.
- Before modifying any file not in your current task scope: ask first.
- If SPEC.md and this file conflict: **SPEC.md wins.** Flag it to me immediately.

---

## Source of Truth

| Question | Answer lives in |
|----------|----------------|
| What to build | `SPEC.md` (features, ACs, data models) |
| How to build it | `CLAUDE.md` (this file) + `RULES.md` |
| Why we're building it | `PROJECT.md` |
| What's done | `RELEASE.md` (last entry) |
| Bugs found but not fixed | `BACKLOG.md` |

---

## Project Structure

```
root/
├── composeApp/                        ← Compose Multiplatform UI
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/feature/
│       │       ├── onboarding/        ← Steps 1–8 (SPEC.md §g)
│       │       ├── reader/            ← Bible reader screens & ViewModels
│       │       ├── plan/              ← Reading plan screens & ViewModels
│       │       ├── catchup/           ← Grace mechanic screens & ViewModels
│       │       ├── bookmarks/         ← Bookmark list & sharing screens
│       │       ├── notifications/     ← Notification preference screens
│       │       └── sync/              ← Auth & account screens
│       ├── androidMain/               ← Android UI overrides & expect/actual
│       └── iosMain/                   ← iOS UI overrides & expect/actual
│
├── shared/                            ← KMP shared module (domain + data)
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/
│       │       ├── domain/
│       │       │   ├── model/         ← Pure Kotlin data models (no platform imports)
│       │       │   ├── repository/    ← Repository interfaces (no platform imports)
│       │       │   └── usecase/       ← Use cases: suspend funs returning Result<T>
│       │       └── data/
│       │           ├── repository/    ← Repository implementations
│       │           ├── local/         ← SQLDelight DAOs
│       │           └── remote/        ← Firebase / Firestore data sources
│       ├── androidMain/               ← Android-specific data source implementations
│       └── iosMain/                   ← iOS-specific data source implementations
│
├── iosApp/                            ← iOS entry point (Swift, minimal — delegates to KMP)
├── SPEC.md                            ← Source of truth for what to build
├── CLAUDE.md                          ← This file
├── RULES.md                           ← Hard constraints
├── BACKLOG.md                         ← Bugs found but not in scope (Claude writes here)
└── RELEASE.md                         ← Release notes (updated at Phase 6)
```

**Key conventions:**
- Feature UI code lives in: `composeApp/src/commonMain/kotlin/feature/[feature-name]/`
- Domain models live in: `shared/src/commonMain/kotlin/domain/model/`
- Repository interfaces live in: `shared/src/commonMain/kotlin/domain/repository/`
- Use cases live in: `shared/src/commonMain/kotlin/domain/usecase/`
- SQLDelight schemas live in: `shared/src/commonMain/sqldelight/`
- Tests mirror source: `shared/src/commonTest/` and `composeApp/src/commonTest/`
- Platform-specific implementations use `expect/actual` — expect in `commonMain`, actual in `androidMain` + `iosMain`

---

## Architecture Pattern: MVI + Clean Architecture

Every screen follows this exact pattern. Do not deviate.

```
UiState         — immutable data class, single source of truth for the screen
Intent          — sealed interface, one entry per user action
ViewModel       — processes Intents, updates state via _uiState.update { }
Screen          — @Composable, observes StateFlow<UiState>, dispatches Intents only
                  ZERO business logic in Screen composables
```

**Canonical ViewModel pattern:**

```kotlin
// shared/commonMain — domain layer (zero platform imports)
class CatchUpViewModel(
    private val recalculateScheduleUseCase: RecalculateScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatchUpUiState())
    val uiState: StateFlow<CatchUpUiState> = _uiState.asStateFlow()

    fun onIntent(intent: CatchUpIntent) {
        when (intent) {
            is CatchUpIntent.SelectStrategy -> handleStrategySelection(intent.strategy)
            is CatchUpIntent.Confirm -> confirmCatchUp()
            is CatchUpIntent.Dismiss -> _uiState.update { it.copy(showCatchUp = false) }
        }
    }

    private fun handleStrategySelection(strategy: CatchUpStrategy) {
        viewModelScope.launch {
            recalculateScheduleUseCase(strategy)
                .onSuccess { assignments -> _uiState.update { it.copy(preview = assignments) } }
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }
}
```

**Canonical Repository interface pattern (domain layer):**

```kotlin
// shared/commonMain/domain/repository — zero platform imports
interface UserPlanRepository {
    fun observeActivePlan(): Flow<UserPlan?>
    suspend fun saveDayComplete(userPlanId: String, dayIndex: Int): Result<Unit>
    suspend fun recalculateSchedule(params: RecalcParams): Result<UserPlan>
}
```

**Canonical expect/actual pattern for platform behaviour:**

```kotlin
// commonMain
expect fun shareText(text: String)
expect fun shareImageCard(bitmap: ImageBitmap)
expect object AppPreferences {
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted()
}

// androidMain
actual fun shareText(text: String) { /* Intent.ACTION_SEND */ }
actual object AppPreferences {
    actual fun hasCompletedOnboarding(): Boolean =
        prefs.getBoolean("has_completed_onboarding", false)
    actual fun setOnboardingCompleted() =
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
}

// iosMain
actual fun shareText(text: String) { /* UIActivityViewController */ }
actual object AppPreferences {
    actual fun hasCompletedOnboarding(): Boolean =
        NSUserDefaults.standardUserDefaults.boolForKey("has_completed_onboarding")
    actual fun setOnboardingCompleted() =
        NSUserDefaults.standardUserDefaults.setBool(true, "has_completed_onboarding")
}
```

---

## Dependency Injection: Koin

Single `appModule` declared in `commonMain`, started from both `AndroidApplication`
and the iOS `@main` entry point. Do not use Hilt — it is Android-only.

```kotlin
val domainModule = module {
    factory { GetBibleChapterUseCase(get()) }
    factory { RecalculateScheduleUseCase(get()) }
    factory { GetBookmarksUseCase(get()) }
    factory { SaveDayCompleteUseCase(get()) }
    factory { GetActivePlanUseCase(get()) }
}

val dataModule = module {
    single<BibleRepository> { BibleRepositoryImpl(get(), get()) }
    single<UserPlanRepository> { UserPlanRepositoryImpl(get(), get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(get(), get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}

val presentationModule = module {
    viewModel { ReaderViewModel(get(), get()) }
    viewModel { PlanViewModel(get(), get(), get()) }
    viewModel { CatchUpViewModel(get()) }
    viewModel { BookmarksViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { SyncViewModel(get()) }
}
```

---

## Architecture Decisions

| Decision | Choice | Reason (from SPEC.md §5) |
|----------|--------|--------------------------|
| Cross-platform | Kotlin Multiplatform + Compose Multiplatform | 100% shared business logic; simultaneous iOS + Android launch |
| DI | Koin (`koin-compose-multiplatform`) | Hilt is Android-only; Koin runs on both targets |
| Async | Kotlin Coroutines + Flow | Native Kotlin async primitive; no additional abstraction |
| Local DB | SQLDelight | Typesafe KMP-compatible SQLite; same query code on both platforms |
| Remote DB | Firestore via `firebase-kotlin-sdk` (GitLive) | Coroutine-native; real-time sync; offline persistence built in |
| Auth | Firebase Auth (Apple + Google + Email + Guest) | Apple Sign-In required on iOS when any social login offered |
| Push | Local notifications only (v1) | No server infra needed; AlarmManager (Android) + UNUserNotificationCenter (iOS) |
| Image cards | Compose Multiplatform GraphicsLayer canvas capture | On-device; no server dependency |
| Bible content | Bundled SQLDelight database | True offline; no CDN; no first-launch download |
| UI pattern | MVI (UiState / Intent / ViewModel / Screen) | Predictable state; zero business logic in composables |
| State exposure | `StateFlow<UiState>` + `collectAsStateWithLifecycle()` | Lifecycle-safe; no LiveData |

1. AppPreferences placement: shared/commonMain (expect) + shared/androidMain + shared/iosMain (actuals).
Android Context: provided via Koin as applicationContext singleton, initialized in AndroidApplication.

2. SQLDelight config:
   - packageName = "app.rema.bible.database"
   - schemaOutputDirectory = shared/src/commonMain/sqldelight
   - Database class name = BibleReaderDatabase

3. Root NavHost lives in composeApp/commonMain/kotlin/App.kt as a single
   App() composable, called from MainActivity (Android) and the iOS @main entry.
   Add App.kt to the project structure in CLAUDE.md.

4. All six ViewModels are included in the Koin presentationModule as listed
   in CLAUDE.md — this takes precedence over the SPEC.md example which was
   illustrative only.



---

## Key Libraries

| Concern | Library | Notes |
|---------|---------|-------|
| UI | Compose Multiplatform (JetBrains) | All screens in commonMain |
| DI | `koin-compose-multiplatform` | Single appModule in commonMain |
| Async | Kotlin Coroutines + Flow | No RxJava, no LiveData, no callbacks |
| Local DB | SQLDelight (with KMP drivers) | Android SQLite driver + iOS native SQLite driver |
| Firebase | `firebase-kotlin-sdk` (GitLive) | KMP-compatible bindings for commonMain |
| Navigation | Compose Multiplatform Navigation (JetBrains) | |
| Serialization | `kotlinx.serialization` | |
| Date/Time | `kotlinx-datetime` | No java.util.Date in commonMain |
| Testing | Kotlin Test + MockK (JVM) + Turbine | Turbine for Flow testing |
| Build | Gradle Version Catalogs + Convention Plugins | |

**Do not add libraries not on this list without architect approval.**

---

## Coding Standards

**Language:** Kotlin (commonMain is pure Kotlin — zero platform imports)

**Style:**
- Formatter: `ktlint` — run before every commit: `./gradlew ktlintFormat`
- Run check: `./gradlew ktlintCheck`
- No `println` or `System.out` in any source file

**Naming:**
- Files: `PascalCase.kt` for classes, `camelCase.kt` for top-level functions
- Functions: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Classes/Interfaces/Objects: `PascalCase`
- SQLDelight query files: `EntityName.sq`
- DayIndex variables: always named `dayIndex`, never `day`, `index`, or `d`
- UiState classes: always named `[Feature]UiState`
- Intent sealed interfaces: always named `[Feature]Intent`

**Comments:**
- Write comments for WHY, not WHAT
- Every public function, interface, and data class needs a KDoc comment
- No commented-out code in commits
- Prohibited words in all UI-facing strings: `manqué`, `raté`, `missed`,
  `failed`, `behind`, `retard` — log a BACKLOG item if you find these anywhere

---

## Critical Domain Rules

These are load-bearing product decisions from SPEC.md. Violating them is a spec violation, not a style issue.

**Grace-first language:** The words `manqué`, `raté`, `missed`, `failed`, `behind`, `retard` must never appear in any user-facing string resource. If implementing a feature requires one of these words, stop and flag it.

**Behind is a technical term only:** The `Behind` state is computed and stored internally. It is never displayed to the user as a count, label, badge, or notification copy. See SPEC.md Glossary.

**COMPLETE day state always wins:** In any sync conflict, a `DayState.COMPLETE` record always overwrites `SKIPPED` or `NOT_YET`. Never write logic that can revert a completed day to incomplete.

**`recalculateSchedule` is a pure function:** It takes inputs, returns a new `DailyAssignment[]`, and mutates nothing. Historical `DailyProgress` records are never deleted or modified by recalculation. Skipped days are marked, not deleted.

**Compress distribution algorithm:** Even division of total missed chapters across the compress window. Remainder chapters are assigned to the final day of the window. Example: 15 missed chapters over 7 days = 2 chapters/day for 6 days + 3 chapters on day 7.

**5-chapter daily cap:** Any Compress option that results in more than 5 chapters/day is hidden from the UI. If all Compress options exceed the cap, the entire Compress section is hidden.

**One notification per day — hard cap:** No feature may schedule more than one local notification per calendar day per user, under any circumstance including active Compress windows.

**`hasCompletedOnboarding` is never reset:** It is set exactly once (at AC-G-8 transition) and never reset by sign-out, account deletion, plan changes, or app updates. It is device-scoped, not account-scoped.

**Onboarding step order is fixed:** Steps 1–8 may not be reordered without re-speccing SPEC.md §g. The grace philosophy screen (Step 2) is non-skippable by design.

**Account deletion is hard-delete:** AC-E-9 requires permanent deletion of all Firestore documents, local SQLDelight records, and the Firebase Auth record. Soft-delete or anonymisation is not acceptable.

---

## Data Models Quick Reference

See SPEC.md §3 for full definitions. Key types for session context:

```kotlin
// DayState — used in DailyProgress
enum class DayState { COMPLETE, SKIPPED, NOT_YET }

// PlanStatus — used in UserPlan
enum class PlanStatus { ACTIVE, PAUSED, COMPLETED, ABANDONED }

// CatchUpStrategy — used in recalculateSchedule()
sealed interface CatchUpStrategy {
    data class Compress(val windowDays: Int) : CatchUpStrategy  // 7 | 14 | 30 | Int.MAX_VALUE (full plan)
    object SkipAndContinue : CatchUpStrategy
    object Recalculate : CatchUpStrategy
}

// Translation
enum class Translation { KJV, LOUIS_SEGOND }

// Language
enum class Language { FR, EN }

// AuthProvider
enum class AuthProvider { APPLE, GOOGLE, EMAIL, GUEST }
```

---

## Testing Requirements

- **Unit tests:** every use case, every repository implementation, every pure function
- **Key priority:** `RecalculateScheduleUseCase` must have exhaustive unit tests covering all CatchUpStrategy variants, the 5-chapter cap logic, and edge cases (1 day behind, 365 days behind, plan complete)
- **Flow tests:** use Turbine for all `Flow<T>` emissions
- **Integration tests:** Firestore sync behaviour (use Firebase emulator)
- **UI tests:** onboarding flow end-to-end, catch-up screen options display
- **Coverage target:** 80% line coverage on `shared/commonMain` minimum
- **Test naming:** describe the behaviour, not the implementation
  - Good: `"recalculateSchedule hides compress option when result exceeds 5 chapters per day"`
  - Bad: `"testRecalculate"`
- **Never:** modify a test to make it pass — fix the implementation

---

## Sync Payload (never sync these)

Bible text, plan assignment schedules, and reading order definitions are **never** part of the sync payload. They are deterministic bundled assets. Only sync:
`activePlanConfig`, `completedDays[]`, `skippedDays[]`, `pausedPlans[]`, `bookmarks[]`, `notificationPrefs`

---

## Environment & Secrets

| Variable | Required | Description |
|----------|----------|-------------|
| `GOOGLE_SERVICES_JSON` | Android | Firebase config — never commit to repo |
| `GOOGLE_SERVICES_PLIST` | iOS | Firebase config — never commit to repo |
| `FIREBASE_APP_ID` | CI | Used in CI pipeline only |

**Never hardcode Firebase config, API keys, or credentials in source files.**
**Use `local.properties` (gitignored) for local dev secrets.**

---

## Deployment

- **Branch strategy:** `main` = production-ready; `develop` = active development; feature branches off `develop`
- **Commit format:** `feat:` / `fix:` / `test:` / `refactor:` / `chore:` / `docs:`
- **Pre-commit:** `./gradlew ktlintCheck && ./gradlew testDebugUnitTest`
- **iOS build:** `./gradlew iosApp:build` — requires Xcode on the build machine
- **Android build:** `./gradlew composeApp:assembleDebug`
- **Release:** Gate 6 checklist in SPEC.md must be complete before any production build

---

## Session Startup Checklist

At the start of every session, before writing a single line of code:

**Step 0 — Branch (mandatory, before anything else):**
Create or check out the feature branch:
```
git checkout -b feature/[spec-feature-id]-[short-description]
# or, if the branch already exists:
git checkout feature/[spec-feature-id]-[short-description]
```
Never work on `main` directly. See RULES.md §Git Rules.

Then tell me:

1. Which SPEC.md feature and AC(s) you are implementing
2. Which files you will create or modify (full paths)
3. Your implementation plan in 3–5 bullets
4. Any ambiguities or spec gaps you notice

Then **wait for my approval** before writing code.

If you find a bug or issue outside your current task scope:
- Add it to `BACKLOG.md` with format: `[Feature] [AC-ID] — description`
- Continue your current task
- Do not fix it
