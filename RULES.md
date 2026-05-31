# RULES.md — Bible Reader
# Hard constraints. Non-negotiable.
# Claude Code reads this alongside CLAUDE.md at the start of every session.
# Source: SPEC.md v1.2 + project architecture decisions.

---

## The Prime Directives

1. **Never modify a file outside your current task scope without asking.**
2. **Never guess at a requirement. Ask.**
3. **Never delete or overwrite existing tests.** Add tests. Fix code.
4. **Never commit secrets, credentials, Firebase config, or API keys.**
5. **Fail loudly.** Raise errors with `Result.failure()`. Never silently swallow exceptions.
6. **Never use the words `manqué`, `raté`, `missed`, `failed`, `behind`, `retard` in any user-facing string.** These are prohibited by the spec. Log a BACKLOG item if found.

---

## Scope Control

Each Claude Code session has **ONE task** from SPEC.md.

Before touching any file, confirm it is:
- The file the task directly requires, OR
- A dependency that the task clearly needs (e.g. a domain model the feature references)

If you find a bug outside your scope: **add it to `BACKLOG.md`, do not fix it.**

Format for BACKLOG entries:
```
[Feature] [AC-ID] — Short description of the issue
Found during: [current feature name]
Priority: P0 / P1 / P2
```

---

## Kotlin & KMP Rules

### Always
- `commonMain` code must have **zero platform imports** — no `android.*`, no `platform.*`, no `UIKit`
- All async operations use **Kotlin Coroutines** — no RxJava, no LiveData, no callbacks
- Repository interfaces return `Flow<T>` for streams and `suspend fun` returning `Result<T>` for one-shot ops
- Use `kotlinx-datetime` for all date/time — no `java.util.Date` or `java.util.Calendar` in commonMain
- Use `kotlinx.serialization` for all serialization — no Gson, no Moshi
- ViewModels expose `StateFlow<UiState>` — consumed via `collectAsStateWithLifecycle()` in Compose
- All `expect` declarations must have `actual` implementations for both `androidMain` and `iosMain`

### Never
- No `lateinit var` in ViewModels — use `StateFlow` initialized at declaration
- No `!!` (non-null assertion) — use `?: return`, `?.let`, or `Result.failure()`
- No `GlobalScope` — use `viewModelScope` or a scoped coroutine
- No `Thread.sleep()` or `delay()` in production code paths
- No `println()` or `System.out.println()` — use structured logging if needed
- No hardcoded strings in `commonMain` Kotlin files — all UI strings go in string resources
- No platform imports in `commonMain` — use `expect/actual` for any platform-specific behaviour

---

## MVI Pattern Rules

Every screen must follow the MVI pattern defined in CLAUDE.md. Specific rules:

- `Screen` composables contain **zero business logic** — only UI rendering and Intent dispatch
- `UiState` is an **immutable data class** — updated only via `_uiState.update { it.copy(...) }`
- `Intent` is a **sealed interface** — one entry per distinct user action
- ViewModels **never** directly call Firestore or SQLDelight — only use cases and repositories
- Use cases **never** call other use cases — compose at the ViewModel level

---

## Domain Rules (spec-enforced, not style)

These rules come directly from SPEC.md. Violating them is a spec violation.

**COMPLETE always wins in sync conflicts.**
When writing conflict resolution logic, `DayState.COMPLETE` always overwrites `SKIPPED` or `NOT_YET`. A completed day can never be reverted to incomplete by any sync operation.

**`recalculateSchedule` is a pure stateless function.**
It must never mutate `DailyProgress` records, delete history, or have side effects. Its signature is fixed in SPEC.md §c Architecture Note. Inputs in, `DailyAssignment[]` out.

**Compress distribution is even with remainder on the last day.**
15 missed chapters over 7 days = [2, 2, 2, 2, 2, 2, 3]. Never front-load. Never round arbitrarily.

**5-chapter daily cap is a hard UI rule.**
Hide Compress options exceeding 5 chapters/day. Never show them, never disable them with a greyed state — hide them entirely.

**One notification per calendar day — absolute hard cap.**
No logic path may result in more than one local notification delivered on any single day.

**`hasCompletedOnboarding` is set once, never reset.**
`AppPreferences.setOnboardingCompleted()` is called exactly once, at the AC-G-8 transition. No other code path calls it. Sign-out, account deletion, and plan changes must never reset it.

**Account deletion is hard-delete (AC-E-9).**
All Firestore documents, all local SQLDelight records scoped to the UID, and the Firebase Auth record must be permanently deleted. Soft-delete, anonymisation, or deferred deletion beyond 72 hours is non-compliant.

---

## SQLDelight Rules

- All database queries are in `.sq` files — no raw SQL strings in Kotlin
- All queries are parameterised — no string interpolation in SQL
- Table and column names use `snake_case`
- Every table has a primary key
- Soft deletes use `deleted_at TIMESTAMP` nullable column (see Bookmark model)
- Never drop a table in a migration — add columns only, or create new tables
- Schema changes require architect approval before implementation

---

## Firebase / Firestore Rules

- Firebase config files (`google-services.json`, `GoogleService-Info.plist`) are **never committed**
- All Firestore writes go through repository implementations — never call Firestore directly from a ViewModel or use case
- Firestore offline persistence must be enabled at app startup on both platforms
- All Firestore reads use `Flow`-based listeners — no one-shot `get()` calls for data that changes
- Sync payload is minimal — never sync Bible text, plan schedules, or reading order definitions (they are bundled assets)
- User data is scoped to UID — every Firestore document path includes the user's UID
- Deletion of user data must be complete: all collections under `users/{uid}/` must be deleted on account deletion

---

## Security Rules

- Never log Firebase UIDs, email addresses, or any PII
- Never log auth tokens, session tokens, or device IDs
- All user input (notes on bookmarks, email/password fields) must be validated before persistence
- Firebase Auth tokens are managed by the SDK — never store or cache them manually
- The `deletedAt` soft-delete timestamp on Bookmark exists for sync propagation only — it is not a substitute for hard deletion during account deletion (AC-E-9 requires hard delete)

---

## Performance Rules

- Bible chapter queries must return in < 1 second (SPEC.md §4 NFR)
- Catch-up schedule recalculation must complete in < 500ms
- App cold start to home screen must be < 2 seconds — do not perform network calls on the critical startup path
- Verse image card generation must complete in < 2 seconds on-device
- Sync after reconnect must complete in < 30 seconds
- No blocking calls on the main thread — all I/O is in `Dispatchers.IO`, UI updates on `Dispatchers.Main`
- No N+1 queries — batch SQLDelight reads; use JOIN where appropriate
- The `BibleVerse` table query must use the index on `(translation, book_id, chapter)` — verify with `EXPLAIN QUERY PLAN`

---

## Localization Rules

- All user-facing strings must have both French and English variants
- French is the default — write the French string first, then the English
- String resource keys use `snake_case`: e.g. `catchup_welcome_message`, `plan_selection_title`
- Prohibited words in any string resource value: `manqué`, `raté`, `missed`, `failed`, `behind`, `retard`
- Date formatting must respect locale: `DD/MM/YYYY` for FR, `MM/DD/YYYY` for EN
- Book names must use locale-appropriate names: "Genèse" in FR, "Genesis" in EN

---

## Testing Rules

- Write the test **alongside** the implementation, not after
- Never modify a test to make it pass — fix the implementation
- `RecalculateScheduleUseCase` requires tests for:
  - Each CatchUpStrategy variant
  - The 5-chapter cap filtering logic
  - Edge case: 1 day behind
  - Edge case: user is further behind than all Compress windows can handle
  - Edge case: plan has only 1 day remaining
  - Idempotency: calling twice with same inputs returns same result
- Use Turbine for all `Flow<T>` tests
- Use the Firebase Local Emulator Suite for Firestore integration tests — never run tests against production Firebase
- Test naming: `"[function] [expected behaviour] [given condition]"`
  - Good: `"recalculateSchedule returns empty compress options when all windows exceed 5 chapters per day"`
  - Bad: `"testRecalculate5ChapterCap"`
- Coverage target: 80% line coverage on `shared/src/commonMain` minimum

---

## Git Rules

- Commit message format: `[type]: [what changed]`
  - `feat:` new feature implementation
  - `fix:` bug fix
  - `test:` adding or fixing tests
  - `refactor:` code change with no behaviour change
  - `chore:` build, config, dependency updates
  - `docs:` CLAUDE.md, RULES.md, SPEC.md, comments only
- One logical change per commit
- Tests must pass before committing: `./gradlew testDebugUnitTest`
- Lint must pass before committing: `./gradlew ktlintCheck`
- Branch naming: `feature/[spec-feature-id]-[short-description]`
  - e.g. `feature/ac-c-catchup-mechanic`, `feature/ac-g-onboarding`

---

## What Requires Architect Approval

**Stop and ask before doing any of these:**

- Changing any data model schema (adding/removing fields, changing types)
- Adding any dependency not listed in CLAUDE.md §Key Libraries
- Modifying the folder structure defined in CLAUDE.md §Project Structure
- Changing the MVI pattern or introducing an alternative state management approach
- Changing the Koin module structure
- Deleting any file
- Changing any string that is user-facing (copy changes are product decisions)
- Implementing any behaviour not covered by a SPEC.md acceptance criterion
- Changing the onboarding step order
- Adding any notification beyond the one-per-day hard cap
- Using soft-delete where the spec requires hard-delete (AC-E-9)
- Making any Firebase security rule change
