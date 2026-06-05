# HANDOFF.md
Generated: 2026-06-05 05:50
Session focus: Feature a — Bible Content (SPEC.md §a) + Koin crash fixes + DB version fix

---

## Accomplished This Session

### Feature a — Bible Content (core)
- `shared/src/commonMain/kotlin/domain/model/BibleBook.kt` — created, domain model for one canonical book (bookId, nameFr, nameEn, totalChapters, canonicalOrder)
- `shared/src/commonMain/kotlin/domain/model/CanonicalBooks.kt` — created, in-memory 66-book Protestant canon with nextBook/previousBook navigation helpers
- `shared/src/commonMain/kotlin/domain/usecase/GetBibleBookUseCase.kt` — created, returns all books or single book by ID from CanonicalBooks (no DB)
- `shared/src/commonMain/kotlin/data/local/DatabaseDriverFactory.kt` — created, expect declaration for platform SQLDelight driver
- `shared/src/androidMain/kotlin/data/local/DatabaseDriverFactory.android.kt` — created, copies bundled asset to databases dir then opens AndroidSqliteDriver
- `shared/src/iosMain/kotlin/data/local/DatabaseDriverFactory.ios.kt` — created, copies bundled resource to App Support dir then opens NativeSqliteDriver
- `shared/src/commonMain/kotlin/data/repository/BibleRepositoryImpl.kt` — created, SQLDelight-backed BibleRepository; observeChapter uses asFlow().mapToList(Dispatchers.IO); type alias BibleVerseRow avoids name clash with domain BibleVerse
- `shared/src/commonMain/kotlin/di/AppModule.kt` — modified, wired BibleRepository + DatabaseDriverFactory + BibleReaderDatabase; all other repo bindings remain commented with feature labels; unneeded use cases commented out
- `composeApp/src/commonMain/kotlin/feature/reader/ReaderViewModel.kt` — modified, implemented NavigateToNextChapter/PreviousChapter with canonical wrap-around; loads GEN 1 on init; removed SaveDayCompleteUseCase (was causing Koin crash)
- `composeApp/src/commonMain/kotlin/feature/reader/ReaderUiState.kt` — modified, added bookName, totalChapters, bookList, showBookList
- `composeApp/src/commonMain/kotlin/feature/reader/ReaderIntent.kt` — modified, added OpenBookList, DismissBookList
- `composeApp/src/commonMain/kotlin/feature/reader/ReaderScreen.kt` — modified, full implementation: verse list, verse selection + reference label (AC-A-3), translation toggle (AC-A-6), prev/next nav (AC-A-4), book list bottom sheet (AC-A-5); split into ReaderScreen(viewModel) + ReaderContent(uiState, onIntent); 8 @Preview functions added

### Bible text asset
- `composeApp/src/androidMain/assets/bible_reader.db` — created, pre-populated SQLite: 31,102 KJV + 31,170 LSG verses across 66 books; PRAGMA user_version = 1
- `iosApp/iosApp/bible_reader.db` — created, same asset for iOS bundle
- `tools/seed_bible_db.py` — created/updated, generates bible_reader.db from ebible.org readaloud zips; now sets PRAGMA user_version = 1
- `tools/.gitignore` — created, ignores generated bible_reader.db in tools/

### AC-G-1 locale detection fix (in-scope per architect decision)
- `shared/src/commonMain/kotlin/AppPreferences.kt` — modified, added getDeviceLocale() expect declaration
- `shared/src/androidMain/kotlin/AppPreferences.android.kt` — modified, added getDeviceLocale() actual; returns FR when prefs == null (JVM test safety)
- `shared/src/iosMain/kotlin/AppPreferences.ios.kt` — modified, added getDeviceLocale() actual using NSLocale.currentLocale.languageCode
- `composeApp/src/commonMain/kotlin/feature/onboarding/OnboardingViewModel.kt` — modified, initial state uses AppPreferences.getDeviceLocale() instead of hardcoded Language.FR

### Compose preview rules + tooling
- `composeApp/build.gradle.kts` — modified, added compose.components.uiToolingPreview (commonMain) and compose.uiTooling (androidMain)
- `RULES.md` — modified, added §Compose UI Rules: @Preview mandatory, 50-line body cap, reuse before duplicating
- `CLAUDE.md` — modified, added decision #5: correct Preview import + Content/Screen split pattern

### Koin crash fixes (NoDefinitionFoundException at startup)
- `composeApp/src/commonMain/kotlin/feature/plan/PlanViewModel.kt` — modified, removed all constructor deps (repos not bound yet)
- `composeApp/src/commonMain/kotlin/feature/bookmarks/BookmarksViewModel.kt` — modified, same
- `composeApp/src/commonMain/kotlin/feature/catchup/CatchUpViewModel.kt` — modified, same
- `composeApp/src/commonMain/kotlin/feature/sync/SyncViewModel.kt` — modified, same
- `composeApp/src/commonMain/kotlin/di/PresentationModule.kt` — modified, updated all viewModel() registrations to match stripped constructors

### App routing + iOS init + SQLite version crash
- `composeApp/src/commonMain/kotlin/App.kt` — modified, replaced Text("Home") stub with ReaderScreen()
- `composeApp/src/iosMain/kotlin/KoinInitializer.kt` — created, initKoin() called from Swift @main
- `iosApp/iosApp/iOSApp.swift` — modified, calls KoinInitializerKt.doInitKoin() on startup
- `composeApp/src/androidMain/kotlin/app/rema/bible/BibleReaderApplication.kt` — modified, registers DatabaseDriverFactory(androidContext()) singleton before shared modules
- `gradle/libs.versions.toml` — modified, added kotlinx-coroutines-test entry
- `shared/build.gradle.kts` — modified, added kotlinx.coroutines.test to commonTest dependencies

### Tests
- `shared/src/commonTest/kotlin/domain/model/CanonicalBooksTest.kt` — created, 15 tests covering 66-book invariants, navigation, chapter counts
- `shared/src/commonTest/kotlin/domain/usecase/GetBibleBookUseCaseTest.kt` — created, 4 tests
- `shared/src/commonTest/kotlin/domain/usecase/GetBibleChapterUseCaseTest.kt` — created, 2 tests using fake repository + Turbine + runTest

---

## Current Feature State

Feature: Bible Content
SPEC.md section: §a

**ACs completed (tests passing):**
- AC-A-1: Full KJV + LSG bundled in asset, accessible offline immediately after install (no download)
- AC-A-2: Chapter queries served from local SQLDelight DB, index on (translation, book_id, chapter)
- AC-A-3: Tapping a verse selects it and shows reference label (e.g. "Genèse 1:1") above the verse text
- AC-A-4: Next/previous chapter nav with canonical book wrap-around (GEN 50 → EXO 1, REV 22 → stops)
- AC-A-5: Book names in French (nameFr) when app language is FR, English (nameEn) otherwise
- AC-A-6: Translation toggle retains same book + chapter in the new translation

**ACs in progress:** None

**ACs not started:** None — all 6 ACs implemented and working

---

## Exact Next Steps

**Start Feature b — Reading Plans (SPEC.md §b)**

```
Begin Feature b — Reading Plans. Feature a (Bible Content) is complete: all
AC-A-1 through AC-A-6 are implemented. Before writing any code:

1. Create branch: git checkout -b feature/ac-b-reading-plans
2. Read SPEC.md §b in full (AC-B-1 through AC-B-10)
3. Read RULES.md §Domain Rules — pay close attention to:
   - COMPLETE always wins in sync conflicts
   - recalculateSchedule is a pure function
   - One active plan at a time
4. Read CLAUDE.md §Architecture Decisions and §Dependency Injection

Key wiring work for Feature b:
- Create UserPlanRepositoryImpl in shared/src/commonMain/kotlin/data/repository/
- Uncomment UserPlanRepository binding in shared/src/commonMain/kotlin/di/AppModule.kt
- Uncomment GetActivePlanUseCase, SaveDayCompleteUseCase in domainModule
- Create CreateUserPlanUseCase (approved by architect, see CLAUDE.md decision #5)
- Add GetActivePlanUseCase + SaveDayCompleteUseCase back to PlanViewModel constructor
- Add UserPlanLocal.sq queries needed for Feature b

Present the full implementation plan and wait for architect approval before writing code.
```

---

## Decisions Made This Session

- Decision: Bible database seeded as pre-populated SQLite asset (copy-on-first-install), not SQLDelight migrations
  Reason: 62,000 INSERTs in a migration would cause significant first-launch latency; asset copy is instantaneous
  Add to CLAUDE.md: Already recorded in Architecture Decisions table

- Decision: PRAGMA user_version must match BibleReaderDatabaseImpl.Schema.version (currently 1) in any pre-populated asset
  Reason: SQLDelight reads user_version to decide whether to call Schema.create(); user_version = 0 crashes on first open
  Add to CLAUDE.md: Add a note under §Architecture Decisions: "Any bundled .db asset must have PRAGMA user_version set to match the SQLDelight schema version before deployment"

- Decision: Scaffold ViewModels must have no constructor dependencies whose repositories are not yet bound in Koin
  Reason: Koin resolves the full dependency chain at ViewModel creation time — unbound repos crash even if the method is never called
  Add to CLAUDE.md: Add under §Dependency Injection: "Only wire ViewModel constructor deps that have a live binding in dataModule. Stub ViewModels take no args until their feature is implemented."

- Decision: @Preview import is org.jetbrains.compose.ui.tooling.preview.Preview, never androidx.*
  Reason: androidx preview annotation is Android-only; breaks commonMain
  Add to CLAUDE.md: Already documented in decision #5

- Decision: Every Screen composable splits into Screen(viewModel) + Content(uiState, onIntent)
  Reason: Previews cannot instantiate a Koin ViewModel; Content composable takes plain data
  Add to CLAUDE.md: Already documented in decision #5

---

## BACKLOG Items Added This Session

- `[BibleContent] [AC-G-5]` (P2) — Translation preview in OnboardingScreen Step 5 still uses hardcoded strings; replace with live BibleRepository queries now that DB is populated
- P0 item `[BibleContent] [AC-A-1]` — added when asset was missing, resolved same session and replaced with above P2 item

At session end, BACKLOG.md contains 7 open items, all P1 or P2. No P0 items.

---

## Test Results at Session End

| Test class | Tests | Failures |
|-----------|-------|----------|
| CanonicalBooksTest | 15 | 0 |
| GetBibleBookUseCaseTest | 4 | 0 |
| GetBibleChapterUseCaseTest | 2 | 0 |
| OnboardingViewModelTest | 27 | 0 |
| **Total** | **48** | **0** |

`BUILD SUCCESSFUL` — no failures, no skipped tests.

Coverage: Not measured (jacoco not configured). All new domain models and use cases have unit tests.

---

## Warnings for Next Session

1. **Uninstall required on physical devices before testing.** The broken asset (user_version = 0) was already copied to `/data/data/app.rema.bible/databases/bible_reader.db` on any device that ran the app before the fix. `copyAssetIfNeeded()` skips recopy if file exists. Must uninstall → reinstall.

2. **All scaffold ViewModels (Plan, Bookmarks, CatchUp, Sync) have empty constructors.** Their `onIntent` bodies throw `TODO("Feature x")`. Navigation to those screens will crash. Feature b must reinstate PlanViewModel dependencies before those screens are reachable.

3. **`CreateUserPlanUseCase` does not exist yet.** Required at AC-G-8 (completeOnboarding → create UserPlan). Architect approved for Feature b scope. Do not create it in any other feature.

4. **ebible.org source zips are in /tmp/ on the dev machine.** `/tmp/kjv_raw/` and `/tmp/lsg_raw/` will not survive a reboot. To regenerate the asset, re-fetch the zips from ebible.org before running `tools/seed_bible_db.py`.

5. **Koin viewModel DSL deprecation warnings** (7 warnings in PresentationModule.kt). Pre-existing issue from Phase 3 scaffold; not blocking. Needs migration to `org.koin.core.module.dsl.viewModel {}` before release.

6. **Feature a has no integration tests against a real SQLDelight driver.** BibleRepositoryImpl is covered only via a fake repository in GetBibleChapterUseCaseTest. An in-memory driver integration test would give higher confidence. Consider adding in a test pass.

7. **iOS Xcode project uses PBXFileSystemSynchronizedRootGroup.** `bible_reader.db` in `iosApp/iosApp/` is auto-included as a bundle resource. Do not add it manually in Xcode — it will create a duplicate entry.
