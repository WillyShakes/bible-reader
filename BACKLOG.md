# BACKLOG.md — Bible Reader
# Bugs and out-of-scope issues found during feature implementation.
# Format: [Feature] [AC-ID] — Description / Found during / Priority

---

[Translation] [AC-G-5] — Step 5 translation preview uses hardcoded John 3:16–18 strings. Will remain hardcoded until the bundled bible_reader.db asset is populated and BibleRepositoryImpl can serve live verse queries. Replace during or after the database seeding milestone.
Found during: Feature g — Onboarding
Priority: P2

[Plan] [AC-B-10] — Mid-plan entry sub-flow (OpenMidPlanEntry intent) is a no-op stub in OnboardingViewModel. Wire to AC-B-10 when Feature b (Reading Plans) is implemented.
Found during: Feature g — Onboarding
Priority: P1

[Plan] [AC-G-8] — UserPlan creation at onboarding completion (selectedPlanType + selectedReadingOrder + startDate) is a TODO stub in OnboardingViewModel.completeOnboarding(). Wire CreateUserPlanUseCase when Feature b is implemented.
Found during: Feature g — Onboarding
Priority: P1

[Notifications] [AC-G-7] — NotificationPermissionButton in OnboardingScreen uses SkipNotifications as a placeholder instead of firing the OS-native permission prompt. Wire via expect/actual when Feature d (Notifications) is implemented.
Found during: Feature g — Onboarding
Priority: P1

[Auth] [AC-G-6] — SignInWithApple, SignInWithGoogle, SignInWithEmail intents advance to step 7 without performing any Firebase Auth operation. Wire Firebase Auth when Feature e (Progress Sync) is implemented.
Found during: Feature g — Onboarding
Priority: P1

[Plan] [AC-G-4a] — Date picker in PlanConfigStep displays a text label instead of a platform-native date picker. Wire via expect/actual before Feature g is considered fully done.
Found during: Feature g — Onboarding
Priority: P2

[Notifications] [AC-G-7] — Time picker in NotificationStep displays a text label instead of a platform-native time picker. Wire via expect/actual before Feature d is implemented.
Found during: Feature g — Onboarding
Priority: P2

[BibleContent] [AC-A-1] — The bundled bible_reader.db SQLite asset is not yet present in the repo (composeApp/src/androidMain/assets/ and iosApp/iosApp/). AC-A-1 and AC-A-2 cannot be verified end-to-end until this asset is provided. DatabaseDriverFactory infrastructure is in place and ready. Source data: KJV (public domain) + Louis Segond 1910 (public domain confirmed). DB schema: BibleVerse table with (translation TEXT, book_id TEXT, chapter INTEGER, verse INTEGER, text TEXT). Translation column values: "KJV" and "LSG".
Found during: Feature a — Bible Content
Priority: P0