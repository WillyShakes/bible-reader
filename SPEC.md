# SPEC.md — Bible Reader
**Version:** 1.2 — Post-Analysis (amended)
**Date:** 2026-05-30
**Status:** Ready for Design & Development Phase — zero open blockers

**Changelog v1.2**
- Closed A-1: Louis Segond 1910 confirmed public domain (ebible.org, 2026-03-11)
- Added `hasCompletedOnboarding` to data model (AppPreferences via KMP `expect/actual`)
- Added `hasCompletedOnboarding` to Glossary
- All open questions resolved — no remaining build blockers

**Changelog v1.1**
- Added AC-E-9: Account deletion (legal requirement — PIPEDA / App Store / Play Store compliance)
- Added Feature g: Onboarding (step-by-step spec with one AC per step)
- Added Section 7: Glossary

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Feature Specifications](#2-feature-specifications)
   - [a. Bible Content](#a-bible-content)
   - [b. Reading Plans](#b-reading-plans)
   - [c. Catch-up / Grace Mechanic](#c-catch-up--grace-mechanic)
   - [d. Notifications](#d-notifications)
   - [e. Progress Sync](#e-progress-sync)
   - [f. Verse Bookmarking & Sharing](#f-verse-bookmarking--sharing)
   - [g. Onboarding](#g-onboarding)
3. [Data Models](#3-data-models)
4. [Non-Functional Requirements](#4-non-functional-requirements)
5. [Technical Constraints & Decisions](#5-technical-constraints--decisions)
6. [Open Questions Log](#6-open-questions-log)
7. [Glossary](#7-glossary)

---

## 1. System Overview

### What This App Is

A grace-first, offline-capable Bible reading app for francophone and anglophone users on iOS and Android. It provides structured reading plans (1-year and 6-month), an intelligent catch-up mechanic that treats missed days as pauses rather than failures, cross-device progress sync, and one-tap verse sharing. It is built French-first, targeting the francophone evangelical community with full English support.

### What This App Is Not (v1 Hard Boundaries)

- No social or group reading features — solo reading only
- No audio Bible
- No commentary, devotionals, footnotes, or study notes
- No translations beyond KJV (English) and Louis Segond 1910 (French)
- No in-app monetization, subscriptions, or paywall
- No church leader or admin dashboard
- No Apple Watch, home screen widget, or web app
- No server-triggered push notifications (local notifications only in v1)

---

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        MOBILE APP                           │
│         (Kotlin Multiplatform + Compose Multiplatform)      │
│                                                             │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │  Bible      │  │  Reading     │  │  Bookmarks &       │ │
│  │  Reader     │  │  Plan        │  │  Sharing           │ │
│  │  (offline)  │  │  Engine      │  │                    │ │
│  └──────┬──────┘  └──────┬───────┘  └────────┬───────────┘ │
│         │                │                    │             │
│  ┌──────▼────────────────▼────────────────────▼───────────┐ │
│  │              Shared KMP Module — SQLDelight Database                      │ │
│  │  (Bible text + progress + bookmarks + plan state)      │ │
│  └──────────────────────────┬──────────────────────────────┘ │
│                             │ sync (when online)             │
│  ┌──────────────────────────▼──────────────────────────────┐ │
│  │           Offline Sync Queue (local)                    │ │
│  └──────────────────────────┬──────────────────────────────┘ │
└─────────────────────────────┼───────────────────────────────┘
                              │ HTTPS
┌─────────────────────────────▼───────────────────────────────┐
│                      FIREBASE (Backend)                      │
│                                                             │
│  ┌──────────────────┐        ┌──────────────────────────┐  │
│  │  Firebase Auth   │        │  Firestore               │  │
│  │  - Apple Sign-In │        │  - UserPlan              │  │
│  │  - Google        │        │  - DailyProgress         │  │
│  │  - Email/Password│        │  - Bookmarks             │  │
│  └──────────────────┘        │  - NotificationPrefs     │  │
│                              └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘

Note: Bible text (KJV + Louis Segond 1910) is fully bundled
in the app binary. No runtime API calls for Bible content.
```

---

## 2. Feature Specifications

---

### a. Bible Content

#### Intent
Provide the complete, navigable text of KJV and Louis Segond 1910 as a fully offline-capable local asset, so users can read anywhere without a data connection.

#### Acceptance Criteria

**AC-A-1**
Given the app has been installed and opened for the first time,
When the user completes onboarding,
Then the full text of both KJV and Louis Segond 1910 (all 66 books, all chapters, all verses) is immediately accessible without any network connection — no download step required.

**AC-A-2**
Given the user is offline,
When they navigate to any book, chapter, or verse in either translation,
Then the content loads within 1 second from local storage, with no network request attempted.

**AC-A-3**
Given the user is on any chapter screen,
When they tap a verse number,
Then that verse is individually selectable and its reference (e.g. "Jean 3:16" or "John 3:16") is displayed in the UI, correctly formatted for the active language.

**AC-A-4**
Given the user is reading a chapter,
When they reach the last verse and swipe forward (or tap "next"),
Then the app navigates to the next chapter; if it is the last chapter of a book, it navigates to the first chapter of the next canonical book.

**AC-A-5**
Given the app UI language is set to French,
When the user browses the book list,
Then book names are displayed in French (e.g. "Genèse", "Matthieu") regardless of which translation they are reading.

**AC-A-6**
Given the user has selected a translation (KJV or Louis Segond),
When they toggle to the other translation,
Then the app navigates to the same book and chapter in the new translation, retaining the user's reading position.

#### Out of Scope (this feature)
- Any translation beyond KJV and Louis Segond 1910
- Parallel / split-screen translation view
- Audio playback
- Commentary, footnotes, cross-references, or study tools
- Runtime Bible content API calls (api.bible used only during development to source and verify text)
- Streaming or on-demand chapter fetching

#### Content Delivery Decision
Both translations are **fully bundled** in the app binary at install time (~15–20 MB per translation, ~35–40 MB total). There is no first-launch download, no CDN dependency, and no content API at runtime. api.bible was used during development only to source and verify the canonical text before bundling.

#### Licensing Note
Louis Segond 1910 is in the public domain. This must be confirmed in writing by the legal lead before the build phase begins. KJV is public domain in all jurisdictions.

---

### b. Reading Plans

#### Intent
Give the user a structured, calendar-bound daily reading schedule across two plan durations and two reading orders, with clear progress visibility that is motivating rather than shaming — and a Free Mode for users who simply want to read without a plan.

#### Acceptance Criteria

**AC-B-1**
Given a user has no active reading plan,
When they open the app for the first time after onboarding,
Then they are presented with a plan selection screen offering three paths: "Bible en 1 an" / "Bible in 1 Year", "Bible en 6 mois" / "Bible in 6 Months", and "Lecture libre" / "Free Reading" (no plan), with an estimated daily reading time shown for each plan option.

**AC-B-2**
Given the user selects a plan,
When they proceed to plan setup,
Then they must choose: (1) a reading order — Canon order (Genesis to Revelation) or Chronological order, and (2) a start date — any calendar date of their choosing.

**AC-B-3**
Given the user confirms their plan and start date,
When that start date arrives,
Then the app generates and stores a full assignment schedule: one reading assignment per calendar day for the plan's duration, covering the entire Bible according to the chosen reading order, with no day left unassigned.

**AC-B-4**
Given a user has an active plan,
When they open the app on any given day,
Then today's assigned passage is displayed on the home screen as the primary call to action, showing the book, chapter range, and estimated read time.

**AC-B-5**
Given a user has read today's assigned passage,
When they tap the completion button,
Then that day is recorded as complete, the progress indicator updates immediately, and the app does not prompt them to read again until the next day's assignment.

**AC-B-6**
Given a user exits the app mid-reading without marking the passage complete,
When they next open the app,
Then a single, non-intrusive prompt asks: "Avez-vous terminé [référence] ?" / "Did you finish [reference]?" with Yes and Not Yet options — shown once per session, not on every open.

**AC-B-7**
Given a user has completed at least one day,
When they view their progress screen,
Then they can see: total days completed, total days in plan, percentage through the Bible, and a calendar heatmap showing read vs. not-yet-read vs. skipped days — with unread and skipped days shown in neutral colours, never red, never with guilt-inducing iconography or language.

**AC-B-8**
Given a user wants to switch from their active plan to a different plan,
When they initiate a plan switch,
Then the app presents three options:
  - Replace: start the new plan now; current plan progress is permanently deleted (requires explicit confirmation warning)
  - Pause & start new: current plan is paused and archived; new plan becomes active; paused plan can be resumed later
  - Cancel: return to current plan unchanged

**AC-B-9**
Given a user resumes a paused plan,
When the schedule recalculates,
Then a fresh daily schedule is generated from the current day forward covering all remaining unread content, a new end date is displayed, and no reference to the pause duration appears anywhere in the UI.

**AC-B-10**
Given a user is starting a plan but has already been reading elsewhere (another app or physical Bible),
When they set up their plan,
Then they can specify their current reading position (e.g. Matthew 5), and the app back-fills all prior content as complete and calculates the remaining schedule forward from that point — with a UI flow that makes this self-explanatory without a tutorial.

**AC-B-11**
Given a user has completed their plan (all days marked read or skipped),
When they view the completion screen,
Then they are congratulated and offered the option to start a new plan or restart the current one, with their completed plan archived in history.

#### Out of Scope (this feature)
- Custom plan creation (user-defined reading order)
- Streak counters or chain metaphors (intentionally excluded — shame risk)
- Red "missed day" indicators or failure language
- Multiple simultaneous active plans
- Group or shared plans
- Thematic, devotional, or book-by-book plan variants (v2)

#### Design Mandate
The progress heatmap must use neutral colours (grey or muted blue) for unread and skipped days. The words "manqué", "raté", "missed", "failed", "behind", and "retard" are prohibited in all UI copy. This is a load-bearing product decision.

---

### c. Catch-up / Grace Mechanic

#### Intent
Ensure that falling behind a reading plan feels like a pause, not a failure — by giving the user agency over how they recover, never surfacing guilt-inducing language or metrics, and making resumption feel like a welcome return rather than a reckoning.

#### Acceptance Criteria

**AC-C-1**
Given a user opens the app and is one or more days behind their reading schedule,
When the home screen loads,
Then the app displays a single, warm message acknowledging the gap (e.g. "Bienvenue ! On reprend là où vous en étiez." / "Welcome back. Let's pick up where you left off.") and presents the catch-up screen — never displaying a count of missed days, never using the words "retard", "behind", "missed", or "failed".

**AC-C-2**
Given a user is behind and views the catch-up screen,
When the catch-up options are presented,
Then the app offers up to four recovery paths (subject to feasibility constraints in AC-C-5):
  1. Compress (7 days): redistribute missed readings over the next 7 days
  2. Compress (14 days): redistribute missed readings over the next 14 days
  3. Compress (30 days): redistribute missed readings over the next 30 days
  4. Compress (full plan): spread missed readings across all remaining plan days
  5. Skip & Continue: mark missed days as skipped (not failed); resume today's originally scheduled passage; end date unchanged
  6. Recalculate: extend the plan end date so today becomes the new on-track day; no extra daily load

**AC-C-3**
Given the user selects a Compress option and confirms,
When the schedule recalculates,
Then the daily assignment for each day in the catch-up window increases proportionally, the total plan content is unchanged, the original end date is preserved (for fixed-window Compress), and the new daily chapter load is shown to the user before they confirm (e.g. "Pendant 14 jours, vous lirez ~3 chapitres par jour").

**AC-C-4**
Given the user selects Skip & Continue,
When the plan resumes,
Then missed days are marked with a neutral "skipped" state (visually distinct from "read" and "not yet"), the user's daily assignment returns to the original volume, and no reference to the skipped days appears on the home screen going forward.

**AC-C-5**
Given a Compress option would result in more than 5 chapters per day,
When the catch-up screen is rendered,
Then that specific Compress window option is hidden from the UI. If all four Compress options exceed the 5-chapter threshold, the entire Compress section is hidden and only Skip & Continue and Recalculate are shown.

**AC-C-6**
Given the user selects Recalculate,
When the new schedule is generated,
Then the plan end date is extended by exactly the number of unread days, today's assignment is the passage that was originally next in the reading order (no doubled-up load), and the user sees the new projected end date before confirming.

**AC-C-7**
Given a user dismisses the catch-up screen without choosing,
When they return to the home screen,
Then today's originally scheduled passage is shown as the primary CTA, a non-intrusive secondary link to the catch-up options remains accessible, and no badge or counter appears on the app icon or tab bar.

**AC-C-8**
Given the user opens the app while behind, on any subsequent open,
When the home screen loads,
Then the catch-up prompt is shown again — it appears every time the app is opened while the user is behind, with no snooze or delay logic.

**AC-C-9**
Given any push notification delivered while the user is behind,
When the notification is composed,
Then the copy is encouraging and forward-looking only (e.g. "📖 Votre lecture du jour vous attend") — it must never reference days missed, a streak broken, or a count of any kind.

#### Architecture Note
The schedule recalculation logic must be a **pure, stateless function** with the signature:

```
recalculateSchedule(
  planType: PlanType,
  readingOrder: ReadingOrder,
  completedDays: DayIndex[],
  skippedDays: DayIndex[],
  today: Date,
  catchUpStrategy: CatchUpStrategy
) → DailyAssignment[]
```

This function must never mutate historical records. Skipped days are marked, never deleted. This is critical for sync integrity across devices.

Compress distribution: even division of total missed chapters across the window, remainder chapters assigned to the final day of the window.

#### Out of Scope (this feature)
- Automatic catch-up without user choice
- Streak counters or broken-chain metaphors
- Penalising skipped days in any metric or display
- Showing missed-day counts anywhere in the UI
- Automatic plan abandonment after N missed days

---

### d. Notifications

#### Intent
Deliver timely, encouraging reading reminders that feel like a gentle invitation rather than an obligation, while fully respecting the user's attention and never compounding the guilt of a missed day.

#### Acceptance Criteria

**AC-D-1**
Given a user has completed their first reading session,
When the completion screen is dismissed,
Then the app presents a native iOS/Android permission prompt for notifications, preceded by a single-screen explanation in the user's language stating that notifications will be used only for daily reading reminders — no marketing.

**AC-D-2**
Given a user has granted notification permission and has an active reading plan,
When they confirm their preferred reading time (default: 7:00 AM, user-adjustable),
Then a local daily notification is scheduled for that time every day for the duration of the plan, with no server call required.

**AC-D-3**
Given the daily notification fires,
When it is delivered to the user's lock screen or notification centre,
Then the copy is forward-looking and encouraging (e.g. "📖 Votre lecture du jour vous attend" / "📖 Your reading for today is ready"), it includes today's book and chapter reference, and tapping it deep-links directly to today's passage — not the app home screen.

**AC-D-4**
Given a user has completed today's reading,
When the scheduled daily notification time arrives,
Then no notification is delivered — the reminder is suppressed for any day already marked as read.

**AC-D-5**
Given a user has an active plan and has not opened the app in 7 days,
When the re-engagement local notification fires,
Then the copy is warm and invitational (e.g. "On pense à vous 🙏" / "We've been thinking of you 🙏"), contains zero reference to days missed or a broken streak, and fires only once per 7-day absence — not repeatedly.

**AC-D-6**
Given a user opens the app after a gap of 2 or more days,
When the catch-up screen is shown,
Then no "you missed X days" notification has been sent during the absence — the app waits for the user to return on their own terms.

**AC-D-7**
Given a user is in Free Mode (no active plan),
When notifications are configured,
Then no daily reading reminder is scheduled; only the 7-day inactivity nudge is available, and it is opt-in.

**AC-D-8**
Given a user disables notifications at the OS system level,
When they open the app,
Then the app does not prompt them to re-enable notifications more than once per 30-day period, and all reading functionality is completely unaffected.

**AC-D-9**
Given a user has an active plan with a Compress catch-up window active (higher daily load),
When the daily notification fires,
Then only one notification is delivered — there is no second mid-day reminder regardless of reading load. One notification per day is a hard cap.

#### Technical Note
All notifications in v1 are **local notifications** scheduled on-device. No server-side push infrastructure is required. Remote/server-triggered push (e.g. re-engagement campaigns) is a v2 item. This eliminates a backend dependency and simplifies the notification permission flow.

#### Out of Scope (this feature)
- Marketing or promotional push notifications
- Server-triggered remote push (v2)
- Notification analytics or open-rate tracking
- Per-book or per-chapter notification customisation
- Apple Watch notification mirroring
- More than one notification per day

---

### e. Progress Sync

#### Intent
Ensure a signed-in user's reading progress, plan state, bookmarks, and preferences are seamlessly available on any device they own, with offline-first behaviour guaranteeing that connectivity issues never interrupt reading.

#### Acceptance Criteria

**AC-E-1**
Given a user launches the app for the first time,
When they reach the account screen during onboarding,
Then they are offered three sign-in options (Apple Sign-In, Google Sign-In, Email/Password) and a "Continue as Guest" option — with a clear, non-pressuring explanation that guest mode means progress is not backed up across devices.

**AC-E-2**
Given a signed-in user marks a day as read on Device A,
When they open the app on Device B within 60 seconds (both online),
Then the completed day is reflected on Device B without any manual sync action, and the home screen shows the correct next passage.

**AC-E-3**
Given a signed-in user marks a day as read while offline,
When their device regains network connectivity,
Then the local progress is automatically synced to Firestore within 30 seconds, with no user action required and no data loss.

**AC-E-4**
Given two devices write conflicting progress records for the same day (both offline, both written independently),
When sync resolves the conflict,
Then the read state always wins over unread — a day marked complete on either device is never reverted to incomplete.

**AC-E-5**
Given a guest user has accumulated local progress and decides to create an account,
When they complete sign-in (any provider),
Then all existing local progress (completed days, skipped days, bookmarks, active plan, notification preferences) is migrated to their new account without loss, and they do not need to re-enter their plan position.

**AC-E-6**
Given a signed-in user signs out,
When sign-out is confirmed,
Then local progress data is retained on the device in read-only state, the user can continue reading as a guest, and re-signing in with the same account restores full sync.

**AC-E-7**
Given the sync service is unreachable (server down or no connectivity),
When the user reads and marks passages complete,
Then the app functions identically to offline mode — all actions are queued locally and sync resumes automatically when the service is available. No error is shown to the user unless the outage exceeds 24 hours, at which point a subtle non-alarming status indicator is shown.

**AC-E-8**
Given a signed-in user updates their notification preferences (reading time, enabled/disabled) on one device,
When sync completes,
Then the updated preferences are reflected on all other signed-in devices within 60 seconds.

**AC-E-9 — Account Deletion (Legal Requirement)**
Given a signed-in user requests account deletion (accessible from Settings),
When they confirm the irreversible deletion via a two-step confirmation (first tap: warning screen explaining what will be lost; second tap: explicit "Delete my account" button — no password re-entry required beyond the platform's existing auth session),
Then all three of the following must complete within 30 seconds and be confirmed to the user in a single success screen before the app returns to the guest state:
  1. **Firestore data:** All documents belonging to the user's UID (UserPlan, DailyProgress, Bookmarks, NotificationPreferences) are permanently deleted from Firestore — not soft-deleted, not anonymised, hard-deleted.
  2. **Local data:** All SQLDelight records scoped to the user's UID are wiped from the device, including cached sync queue entries. The device is left in the same state as a fresh install.
  3. **Firebase Auth record:** The user's Firebase Auth account is deleted, invalidating all active sessions on all devices. Any other signed-in device is signed out within 60 seconds via Firebase Auth token revocation.

> **Legal basis:** Account deletion with full data erasure is required under PIPEDA (Canada), App Store Review Guidelines §5.1.1, and Google Play Policy. The 30-second completion target is a UX target; if deletion is deferred server-side (e.g. Firestore background deletion), the user must see a "Deletion in progress — you will receive no further communications" confirmation immediately, with actual erasure completing within 72 hours maximum.

#### Sync Payload Definition
Only the following data travels over the wire — Bible text and plan schedules are deterministic on-device and never synced:

| Field | Notes |
|---|---|
| `activePlanConfig` | planType, readingOrder, startDate, currentPosition |
| `completedDays[]` | Array of day indices marked read |
| `skippedDays[]` | Array of day indices marked skipped |
| `pausedPlans[]` | Archived paused plan configs and their progress |
| `bookmarks[]` | verseRef, text, translation, note, savedAt |
| `notificationPrefs` | readingTime, enabled, reEngagementEnabled |

#### Out of Scope (this feature)
- Google or Facebook auth beyond what is specified
- Anonymous account merging across different provider identities
- Real-time collaborative or group reading
- Sync to web app (no web app in v1)
- Admin access to user data
- Manual conflict resolution UI

---

### f. Verse Bookmarking & Sharing

#### Intent
Let the user capture and share any verse with the minimum number of taps, making the one-tap WhatsApp-share moment feel instant and beautiful — from anywhere in the app.

#### Acceptance Criteria

**AC-F-1**
Given a user is reading any passage,
When they tap and hold on any verse,
Then that verse is highlighted and a contextual action menu appears within 300ms with two primary options: Bookmark and Share.

**AC-F-2**
Given a user selects a verse and taps Bookmark,
When the bookmark is saved,
Then the verse reference, full text (in the currently active translation), and an optional plain-text note field are stored locally and synced to their account if signed in — and a subtle confirmation is shown without interrupting the reading flow.

**AC-F-3**
Given a user wants to bookmark a multi-verse passage,
When they long-press a start verse and drag to an end verse,
Then the entire range is selectable as a single bookmark (e.g. Jean 3:16–21), stored as one entry with the full text of all selected verses.

**AC-F-4**
Given a user opens their Bookmarks list,
When the list is displayed,
Then bookmarks appear in reverse-chronological order by default, each showing: the reference, the first line of text, the translation label (KJV or Louis Segond), the date saved, and a preview of the note if one exists — and the list is fully available offline.

**AC-F-5**
Given a user taps Share on a selected verse (from the reader or from the Bookmarks list),
When the share sheet appears,
Then it offers two share formats: (1) plain text with the verse text and reference (e.g. «Car Dieu a tant aimé le monde…» — Jean 3:16, Louis Segond 1910), and (2) a verse image card. The user picks one and the native iOS/Android share sheet opens pre-populated.

**AC-F-6**
Given a user selects the verse image card format,
When the card is generated,
Then it is produced entirely on-device (no server call), offers a choice of 5 pre-defined background presets (bundled assets), includes the verse text and reference in the app's typography, displays the translation name, and is ready to share within 2 seconds.

**AC-F-7**
Given a user has bookmarks from multiple translations,
When they view the Bookmarks list,
Then each bookmark clearly displays which translation it was saved from, and bookmarks are never de-duplicated or merged across translations.

**AC-F-8**
Given a user deletes a bookmark,
When deletion is confirmed (with a single confirmation prompt),
Then the bookmark is removed from local storage and from the synced account, and the deletion is reflected on all other signed-in devices within 60 seconds.

**AC-F-9**
Given a user taps Share directly from the Bookmarks list,
When the share sheet appears,
Then the full share flow (plain text or image card) is available without navigating back to the reader.

#### Out of Scope (this feature)
- Bookmark folders or collections (v2)
- Bookmark search (v2)
- Rich text or formatted notes on bookmarks
- Social platform integrations beyond the native OS share sheet
- Server-side image card generation
- Public or shared bookmark lists

---

### g. Onboarding

#### Intent
Give every new user — whether they intend to follow a plan or simply explore — a first experience that immediately communicates grace, removes friction, and puts them inside the Bible within two minutes of install.

#### Design Mandate
Onboarding is the first contact the user has with the app's grace-first philosophy. Every screen must be warm, brief, and forward-moving. There are no dark patterns, no forced account creation, no permission requests before value is demonstrated. The sequence below is fixed; screens may not be reordered without re-speccing this section.

---

#### Step 1 — Welcome & Language Selection

**AC-G-1**
Given a user opens the app for the very first time,
When the welcome screen appears,
Then it displays: the app name, a single-sentence value proposition in the device's system language (French if the device locale is `fr-*`, English otherwise), and a language selector offering Français and English — with the device language pre-selected. Tapping Continue with the selected language persists it as `preferredLanguage` and advances to Step 2. No account prompt, no permission request, no marketing copy appears on this screen.

---

#### Step 2 — Grace Philosophy Statement

**AC-G-2**
Given the user has confirmed their language,
When Step 2 appears,
Then the screen displays a single, full-screen statement of the app's core philosophy — written in plain, warm language, not marketing language — explaining that missing a day is not a failure, that the app is designed to meet them where they are, and that they are in control of their pace. The statement must fit on one screen without scrolling at the default Dynamic Type size. There is one button: "Continuer" / "Continue". There is no "Skip" option — this screen is non-skippable because it sets the expectation that everything else is built on.

---

#### Step 3 — Reading Mode Selection

**AC-G-3**
Given the user has read the grace philosophy statement,
When Step 3 appears,
Then they are presented with three clearly labelled paths:
  - **Plan 1 an / 1-Year Plan** — with estimated daily reading time (~15 min)
  - **Plan 6 mois / 6-Month Plan** — with estimated daily reading time (~25 min)
  - **Lecture libre / Free Reading** — described as "no plan, no pressure — read what you want, when you want"

Selecting any path advances to the next step appropriate to that path (Step 4a for plan paths, Step 4b for Free Reading). The user may return to this screen via a back gesture without losing their selection.

---

#### Step 4a — Plan Configuration (Plan paths only)

**AC-G-4a**
Given the user selected a 1-year or 6-month plan,
When Step 4a appears,
Then they are presented with two configuration choices on a single screen:
  1. **Reading order:** Canon (Genèse → Apocalypse) or Chronological — each with a one-line description
  2. **Start date:** a date picker defaulting to today, allowing any date selection

Below the configuration, a "Mid-plan entry" option is accessible via a secondary link ("Je lis déjà la Bible" / "I'm already reading the Bible"), which opens the mid-plan entry sub-flow (AC-B-10). Tapping "Commencer" / "Start" with the configuration set advances to Step 5.

---

#### Step 4b — Free Reading Entry (Free Reading path only)

**AC-G-4b**
Given the user selected Free Reading,
When Step 4b appears,
Then the screen confirms their choice with an encouraging message (e.g. "Parfait. Lisez à votre rythme." / "Perfect. Read at your own pace."), shows a preview of the book navigation they are about to enter, and presents a single "Commencer à lire" / "Start Reading" button that advances directly to Step 5 — skipping all plan-configuration steps. No plan is created, no schedule is generated.

---

#### Step 5 — Translation Selection

**AC-G-5**
Given the user has completed plan or free reading configuration,
When Step 5 appears,
Then they are asked to choose their primary reading translation: Louis Segond 1910 or KJV — with the translation matching their chosen language (Louis Segond for French users, KJV for English users) pre-selected, but either option always available regardless of language. A short excerpt (3 verses of John 3:16–18) is displayed live in the selected translation as a preview, updating instantly when the selection changes. Tapping "Confirmer" / "Confirm" persists the selection and advances to Step 6.

---

#### Step 6 — Account Creation (Optional)

**AC-G-6**
Given the user has confirmed their translation,
When Step 6 appears,
Then the screen explains in one sentence the benefit of an account ("Synchronisez votre lecture sur tous vos appareils" / "Keep your progress on every device") and offers three sign-in options (Apple Sign-In, Google Sign-In, Email/Password) plus a clearly visible "Continuer sans compte" / "Continue without account" link — styled as a peer option, not a lesser fallback. No sign-in option is pre-selected. No guilt language ("You'll lose your progress!") appears. Completing sign-in or tapping Continue without account both advance to Step 7.

---

#### Step 7 — Notification Permission

**AC-G-7**
Given the user has completed or skipped account creation,
When Step 7 appears,
Then the screen explains what daily notifications will be used for (reading reminders only, no marketing), shows the default time (7:00 AM) with an inline time picker to adjust it now, and presents two options: "Activer les rappels" / "Enable reminders" (triggers the OS-native permission prompt) and "Pas maintenant" / "Not now" (skips without triggering the OS prompt, preserving the ability to enable later). If the user taps "Enable reminders" and the OS prompt is denied, the app acknowledges gracefully ("Pas de problème — vous pouvez activer ça plus tard dans les réglages" / "No problem — you can turn this on later in Settings") and advances to Step 8 without friction.

---

#### Step 8 — First Reading

**AC-G-8**
Given the user has completed all previous onboarding steps,
When Step 8 appears,
Then the user lands directly on today's assigned passage (for plan users) or the book-selection screen (for Free Reading users) — not a dashboard, not a settings screen, not a "you're all set!" splash screen. For plan users, a single tooltip or coach mark (dismissable, shown once only) indicates the "Mark as read" button. Onboarding is considered complete the moment this screen is visible; all subsequent app opens go directly to the home screen.

---

#### Onboarding Step Summary

| Step | Screen | Plan path | Free Reading path | Skippable |
|---|---|---|---|---|
| 1 | Welcome & language | ✅ | ✅ | No |
| 2 | Grace philosophy | ✅ | ✅ | No |
| 3 | Reading mode selection | ✅ | ✅ | No |
| 4a | Plan configuration | ✅ | — | No |
| 4b | Free reading entry | — | ✅ | No |
| 5 | Translation selection | ✅ | ✅ | No |
| 6 | Account creation | ✅ | ✅ | Yes (Continue without account) |
| 7 | Notification permission | ✅ | ✅ | Yes (Not now) |
| 8 | First reading | ✅ | ✅ | N/A — destination, not a step |

#### Out of Scope (this feature)
- Tutorial overlays beyond the single Step 8 coach mark
- Animated onboarding carousels or video
- Onboarding re-entry (if the user force-quits mid-onboarding, they resume at the last completed step)
- A/B testing onboarding variants (v2)
- Push notification permission on iOS before Step 7 (OS prompt must not fire earlier)

---

### User
```
User {
  uid:              String          // Firebase Auth UID (primary key)
  provider:         AuthProvider    // APPLE | GOOGLE | EMAIL | GUEST
  email:            String?         // nullable for Apple/Google
  displayName:      String?
  createdAt:        Timestamp
  lastSeenAt:       Timestamp
  preferredLanguage: Language       // FR | EN
}
```

### AppPreferences (platform-native, never synced)
`hasCompletedOnboarding` is a device-local UI flag stored in platform preferences — **not** in SQLDelight and **not** in the SyncPayload. It is read once on cold start to decide whether to show the onboarding flow or go directly to the home screen.

```kotlin
// commonMain — expect declaration
expect object AppPreferences {
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted()
}

// androidMain — actual implementation (SharedPreferences)
actual object AppPreferences {
    actual fun hasCompletedOnboarding(): Boolean =
        prefs.getBoolean("has_completed_onboarding", false)
    actual fun setOnboardingCompleted() =
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
}

// iosMain — actual implementation (UserDefaults)
actual object AppPreferences {
    actual fun hasCompletedOnboarding(): Boolean =
        NSUserDefaults.standardUserDefaults.boolForKey("has_completed_onboarding")
    actual fun setOnboardingCompleted() =
        NSUserDefaults.standardUserDefaults.setBool(true, "has_completed_onboarding")
}
```

`setOnboardingCompleted()` is called exactly once: at the transition into Step 8 (AC-G-8), when the user lands on their first reading screen. It is never reset by sign-out, plan changes, or account deletion — onboarding is a device-level event, not an account-level one. A user who deletes their account and continues as a guest does not see onboarding again on the same device.

### ReadingPlan (static content asset — not per-user)
```
ReadingPlan {
  planId:           String          // e.g. "canon_1yr", "chrono_6mo"
  planType:         PlanType        // ONE_YEAR | SIX_MONTHS
  readingOrder:     ReadingOrder    // CANON | CHRONOLOGICAL
  totalDays:        Int             // 365 | 180
  assignments:      DailyAssignment[]
}

DailyAssignment {
  dayIndex:         Int             // 1-based
  passages:         Passage[]       // one or more passage ranges
}

Passage {
  bookId:           String          // canonical book identifier
  chapterStart:     Int
  verseStart:       Int?
  chapterEnd:       Int
  verseEnd:         Int?
}
```

### UserPlan
```
UserPlan {
  userPlanId:       String          // UUID
  uid:              String          // FK → User
  planId:           String          // FK → ReadingPlan
  status:           PlanStatus      // ACTIVE | PAUSED | COMPLETED | ABANDONED
  startDate:        Date
  entryPosition:    DayIndex?       // if user joined mid-plan (back-fill feature)
  currentDayIndex:  Int
  projectedEndDate: Date            // recalculated on any schedule change
  createdAt:        Timestamp
  updatedAt:        Timestamp
}
```

### DailyProgress
```
DailyProgress {
  progressId:       String          // UUID
  uid:              String          // FK → User
  userPlanId:       String          // FK → UserPlan
  dayIndex:         Int
  state:            DayState        // COMPLETE | SKIPPED | NOT_YET
  completedAt:      Timestamp?
  deviceId:         String          // for conflict resolution
  updatedAt:        Timestamp
}

// Conflict resolution rule: COMPLETE always wins over SKIPPED or NOT_YET.
// Last-write-wins for COMPLETE vs COMPLETE (idempotent — same outcome).
```

### Bookmark
```
Bookmark {
  bookmarkId:       String          // UUID
  uid:              String          // FK → User
  translation:      Translation     // KJV | LOUIS_SEGOND
  bookId:           String
  chapterStart:     Int
  verseStart:       Int
  chapterEnd:       Int             // same as chapterStart for single verse
  verseEnd:         Int             // same as verseStart for single verse
  text:             String          // full verse text at time of saving
  note:             String?         // optional plain-text user note
  savedAt:          Timestamp
  deletedAt:        Timestamp?      // soft delete for sync propagation
}
```

### NotificationPreferences
```
NotificationPreferences {
  uid:              String          // FK → User
  dailyReminderEnabled:  Boolean
  dailyReminderTime:     LocalTime  // e.g. "07:00"
  reEngagementEnabled:   Boolean
  updatedAt:             Timestamp
}
```

---

## 4. Non-Functional Requirements

### Performance
| Metric | Target |
|---|---|
| Bible chapter load time (offline) | < 1 second |
| App cold start to home screen | < 2 seconds |
| Verse image card generation | < 2 seconds (on-device) |
| Progress sync after reconnect | < 30 seconds |
| Cross-device sync (both online) | < 60 seconds |
| Catch-up schedule recalculation | < 500ms |

### Accessibility
- Minimum standard: **WCAG 2.1 AA** on both platforms
- Dynamic Type / font scaling: all text must reflow correctly at all system font sizes (iOS) and font scale settings (Android)
- VoiceOver (iOS) and TalkBack (Android): all interactive elements must have meaningful accessibility labels
- Minimum tap target size: 44×44pt (iOS HIG) / 48×48dp (Material)
- Colour contrast ratio: minimum 4.5:1 for all body text
- The app must never rely on colour alone to convey meaning (applies especially to the progress heatmap — neutral states must be distinguishable by shape or label, not colour only)

### Platform
**Both iOS and Android at v1 launch — simultaneously.**
- iOS minimum version: iOS 16
- Android minimum version: Android 10 (API 29)
- The app must be functionally and visually equivalent on both platforms. Platform-native interactions (e.g. share sheet, notification permission dialogs) should use platform conventions.

### Localization
- **French-first:** all copy is written in French first, then translated to English. French is the default language.
- Language follows the device system language (FR or EN). User can override in settings.
- All Bible book names, UI labels, notification copy, and error messages must be available in both languages.
- Right-to-left layout is out of scope for v1.
- Date formats must respect locale (DD/MM/YYYY for French, MM/DD/YYYY for English).

### Offline Behaviour
- All reading functionality must work with zero network connectivity, indefinitely.
- Sync is opportunistic — the app never blocks the user waiting for a network response.
- Local SQLite is the source of truth. Firestore is the sync target.

---

## 5. Technical Constraints & Decisions

### Stack Decision: Kotlin Multiplatform (KMP)
**Decision: Kotlin Multiplatform + Compose Multiplatform + Clean Architecture + Koin + Coroutines**

Rationale:
- Simultaneous iOS and Android launch requires a cross-platform solution
- KMP shares 100% of business logic (domain + data layers) in pure Kotlin — the strongest possible guarantee of behavioural parity between platforms
- Compose Multiplatform covers the UI layer on both platforms from a single codebase, while still allowing platform-specific overrides where needed (e.g. native share sheet, notification permission dialogs)
- Koin is preferred over Hilt for KMP because Hilt is Android-only; Koin runs on both targets with no platform conditionals in the DI graph
- Coroutines + Flow is the native async primitive for Kotlin — no additional abstraction layer needed
- The domain layer contains zero platform imports — pure Kotlin only — making it trivially testable with JUnit/Kotlin Test on both targets

React Native and Flutter were considered and rejected: React Native requires a JavaScript bridge that adds latency to the offline-critical Bible reader; Flutter's Firebase ecosystem and SQLite support on iOS are less mature than the Kotlin-native equivalents.

---

### Module Structure (Clean Architecture)

```
root/
├── composeApp/                   ← Compose Multiplatform UI (androidMain / iosMain / commonMain)
│   └── commonMain/
│       └── feature/
│           ├── reader/           ← Bible reader screens & ViewModels
│           ├── plan/             ← Reading plan screens & ViewModels
│           ├── catchup/          ← Grace mechanic screens & ViewModels
│           ├── bookmarks/        ← Bookmark list & sharing screens
│           ├── notifications/    ← Notification preference screens
│           └── sync/             ← Auth & account screens
│
├── shared/                       ← KMP shared module (domain + data)
│   ├── commonMain/
│   │   ├── domain/
│   │   │   ├── model/            ← Pure Kotlin data models
│   │   │   ├── repository/       ← Repository interfaces (no platform imports)
│   │   │   └── usecase/          ← Use cases (suspend funs returning Result<T>)
│   │   └── data/
│   │       ├── repository/       ← Repository implementations
│   │       ├── local/            ← SQLDelight DAOs
│   │       └── remote/           ← Firebase / Firestore data sources
│   ├── androidMain/              ← Android-specific data source implementations
│   └── iosMain/                  ← iOS-specific data source implementations
│
└── iosApp/                       ← iOS entry point (Swift, minimal — delegates to KMP)
```

---

### Dependency Injection: Koin

Koin is used across the full shared module and all Compose Multiplatform screens. A single `appModule` is declared in `commonMain` and started from both `AndroidApplication` and the iOS `@main` entry point.

```kotlin
// shared/commonMain — domain + data modules
val domainModule = module {
    factory { GetBibleChapterUseCase(get()) }
    factory { RecalculateScheduleUseCase(get()) }
    factory { GetBookmarksUseCase(get()) }
}

val dataModule = module {
    single<BibleRepository> { BibleRepositoryImpl(get(), get()) }
    single<UserPlanRepository> { UserPlanRepositoryImpl(get(), get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(get(), get()) }
}

// composeApp/commonMain — presentation module
val presentationModule = module {
    viewModel { ReaderViewModel(get(), get()) }
    viewModel { PlanViewModel(get(), get(), get()) }
    viewModel { CatchUpViewModel(get()) }
    viewModel { BookmarksViewModel(get(), get()) }
}
```

---

### Async: Coroutines + Flow

All async operations use Kotlin Coroutines. Repository interfaces return `Flow<T>` for reactive streams and `suspend fun` returning `Result<T>` for one-shot operations. No RxJava, no LiveData, no callbacks.

```kotlin
// Repository interface — domain layer, zero platform imports
interface UserPlanRepository {
    fun observeActivePlan(): Flow<UserPlan?>
    suspend fun saveDayComplete(userPlanId: String, dayIndex: Int): Result<Unit>
    suspend fun recalculateSchedule(params: RecalcParams): Result<UserPlan>
}
```

ViewModels use `viewModelScope` (AndroidX ViewModel in shared via `lifecycle-viewmodel` KMP artifact) and expose `StateFlow<UiState>` consumed by Compose screens via `collectAsStateWithLifecycle()`.

---

### UI: Compose Multiplatform

All screens are written as `@Composable` functions in `composeApp/commonMain`. Platform-specific behaviour (share sheet, notification permission prompt, image card save) is handled via `expect/actual` declarations.

```kotlin
// commonMain — expect declaration
expect fun shareText(text: String)
expect fun shareImageCard(bitmap: ImageBitmap)

// androidMain — actual implementation
actual fun shareText(text: String) { /* Android Intent.ACTION_SEND */ }

// iosMain — actual implementation
actual fun shareText(text: String) { /* UIActivityViewController */ }
```

MVI pattern is enforced across all screens:
- `UiState` — immutable data class, single source of truth
- `Intent` — sealed interface, all user actions
- `ViewModel` — processes intents, updates state via `_uiState.update { }`
- `Screen` composable — observes state, dispatches intents, zero business logic

---

### Bible Content Source
**Decision: Bundled SQLDelight database, no runtime API**

Both KJV and Louis Segond 1910 are pre-processed into a SQLDelight database and shipped inside the app binary. SQLDelight generates typesafe Kotlin APIs from SQL schema and runs on both Android (via Android SQLite driver) and iOS (via native SQLite driver) with the same query code.

api.bible was used during development only to source and verify the canonical text. There are no runtime API calls for Bible content.

```sql
-- BibleVerse.sq (SQLDelight)
CREATE TABLE BibleVerse (
  translation TEXT NOT NULL,   -- 'KJV' | 'LS1910'
  book_id     TEXT NOT NULL,
  chapter     INTEGER NOT NULL,
  verse       INTEGER NOT NULL,
  text        TEXT NOT NULL,
  PRIMARY KEY (translation, book_id, chapter, verse)
);

getChapter:
SELECT * FROM BibleVerse
WHERE translation = ? AND book_id = ? AND chapter = ?
ORDER BY verse ASC;
```

---

### Auth Provider
**Decision: Firebase Auth with three providers**
- Apple Sign-In — required on iOS per App Store guidelines when any social login is offered
- Google Sign-In — primary for Android; also available on iOS
- Email/Password — fallback for users without Apple ID or Google account
- Guest mode — fully functional, no account required, local SQLDelight storage only

Firebase Auth is accessed from the shared KMP module via the `firebase-kotlin-sdk` (GitLive) which provides KMP-compatible Firebase bindings for both targets.

---

### Backend & Sync
**Decision: Firebase (Firestore + Firebase Auth) via firebase-kotlin-sdk**
- Firestore for real-time cross-device sync
- `firebase-kotlin-sdk` (GitLive) provides coroutine-native Firestore APIs usable from `commonMain`
- Offline persistence enabled on both platforms via Firestore SDK
- Conflict resolution: COMPLETE day state always wins — applied as an application-level rule on write
- Firebase Spark free tier is sufficient for v1 scale; upgrade path to Blaze is straightforward

---

### Push Notifications
**Decision: Local notifications only (v1)**
- Android: `AlarmManager` + `NotificationCompat` via `androidMain` actual implementation
- iOS: `UNUserNotificationCenter` via `iosMain` actual implementation
- Scheduling logic (which day, what time, what copy) lives in `commonMain` as a pure Kotlin function; platform `actual` functions execute the schedule
- No server-side push infrastructure required in v1
- Remote/server-triggered push (FCM + APNs) is a v2 item

---

### Image Card Generation
**Decision: On-device, Compose Multiplatform canvas capture**
- Verse cards rendered as an offscreen `@Composable` and captured to `ImageBitmap` using Compose Multiplatform's `GraphicsLayer` API
- 5 background presets bundled as static assets in `composeApp/commonMain/resources`
- Captured bitmap passed to platform `actual fun shareImageCard(bitmap: ImageBitmap)` for native share sheet
- No server or CDN dependency

---

### Key KMP Libraries

| Concern | Library |
|---|---|
| UI | Compose Multiplatform (JetBrains) |
| DI | Koin (`koin-compose-multiplatform`) |
| Async | Kotlin Coroutines + Flow |
| Local DB | SQLDelight (with KMP drivers) |
| Firebase | firebase-kotlin-sdk (GitLive) |
| Navigation | Compose Multiplatform Navigation (JetBrains) |
| Serialization | kotlinx.serialization |
| Date/Time | kotlinx-datetime |
| Testing | Kotlin Test + MockK (JVM) + Turbine |
| Build | Gradle Version Catalogs + Convention Plugins |

---

## 6. Open Questions Log

All questions raised during the feature-by-feature spec process. Items marked ✅ are resolved. Items marked ⚠️ remain open for the team to confirm before build begins.

| ID | Feature | Question | Status | Decision |
|---|---|---|---|---|
| A-1 | Bible Content | Louis Segond 1910 licensing — confirm public domain in writing | ✅ | **Confirmed public domain.** Source: ebible.org/fraLSG/copyright.htm — "Cette Bible est dans le domaine public. Il n'est pas protégé par copyright." Certified by eBible.org, source files dated 2026-03-11. Verified 2026-05-30. No further legal action required. |
| A-2 | Bible Content | Bundle vs. download on first launch | ✅ | Bundle at install — no download step |
| A-3 | Bible Content | Parallel translation view vs. toggle | ✅ | One translation at a time, toggle |
| A-4 | Bible Content | api.bible as runtime API vs. dev reference | ✅ | Dev reference only — no runtime API |
| B-1 | Reading Plans | Canon order vs. chronological | ✅ | Both offered at plan setup — user chooses |
| B-2 | Reading Plans | Switch plan mid-stream | ✅ | Three options: Replace, Pause & Start New, Cancel |
| B-3 | Reading Plans | Mark as read: manual vs. inferred | ✅ | Manual tap for v1; re-prompt on next open if exited mid-read |
| B-4 | Reading Plans | Plan start date flexibility | ✅ | Any day of the year |
| B-5 | Reading Plans | Paused plan resumption — catch-up debt vs. fresh schedule | ✅ | Fresh schedule recalculation from today; pause gap silently absorbed |
| C-1 | Catch-up | "Behind" trigger — calendar-strict vs. grace-first | ✅ | Hybrid: tracked privately, surfaced gently on open after gap |
| C-2 | Catch-up | Compress window options | ✅ | 7 days, 14 days, 30 days, full plan remainder |
| C-3 | Catch-up | Maximum behind threshold | ✅ | Hide Compress options that exceed 5 chapters/day; hide all Compress if all exceed threshold |
| C-4 | Catch-up | Catch-up prompt frequency | ✅ | Every open while behind; no snooze |
| D-1 | Notifications | Permission prompt timing | ✅ | Deferred — after first reading session complete |
| D-2 | Notifications | Local vs. remote push | ✅ | Local only for v1; remote push is v2 |
| D-3 | Notifications | Re-engagement inactivity threshold | ✅ | 7 days |
| D-4 | Notifications | Default notification time | ✅ | 7:00 AM, user-adjustable |
| D-5 | Notifications | Multiple notifications on Compress days | ✅ | Hard cap: one notification per day |
| E-1 | Sync | Auth provider | ✅ | Apple Sign-In + Google Sign-In + Email/Password + Guest |
| E-2 | Sync | Backend | ✅ | Firebase (Firestore + Auth) |
| E-3 | Sync | Account creation mandatory? | ✅ | Fully optional, never forced |
| E-4 | Sync | Notification prefs in sync payload? | ✅ | Yes — included in sync |
| E-5 | Sync | Android timeline | ✅ | Simultaneous iOS + Android v1 launch — addressed by KMP |
| E-5b | Sync | Email/password as third auth option? | ✅ | Yes — included as fallback |
| F-1 | Bookmarking | Single verse vs. range | ✅ | Both: single verse and manual range |
| F-1b | Bookmarking | Bookmark notes | ✅ | Simple optional plain-text note field — in scope for v1 |
| F-2 | Sharing | Plain text vs. image card | ✅ | Both offered; user chooses at share time |
| F-3 | Sharing | Image card background style | ✅ | 5 pre-defined bundled presets; style TBD by design |
| F-4 | Sharing | Share from Bookmarks list? | ✅ | Yes — full share flow available from Bookmarks list |

### All Items Resolved

As of 2026-05-30, all open questions are closed. There are no remaining blockers for the build phase.

**A-1 — Louis Segond 1910 Licensing** was the last open item. Confirmed public domain via ebible.org/fraLSG/copyright.htm (eBible.org certified, source dated 2026-03-11). The full bilingual declaration — "Cette Bible est dans le domaine public. Il n'est pas protégé par copyright." — is on record. The URL and date should be retained in the project's legal folder for App Store and Play Store submission documentation.

---

## 7. Glossary

Canonical definitions for terms used throughout this document. When a term appears in a feature spec, AC, or data model, it carries exactly the meaning defined here — no synonyms, no approximations.

---

**Assignment** (`DailyAssignment`)
The specific Bible passage or set of passages allocated to a single day in a reading plan. An assignment is a function of the plan type, reading order, and day index — it is deterministic and never stored per-user; it is always computed from the static plan asset. Example: Day 47 of the canon 1-year plan might assign Genesis 48–50.

---

**Behind**
The state in which a user's `currentDayIndex` in their active UserPlan is less than the number of calendar days elapsed since their plan's start date (or recalculated start). A user is Behind when there are unread, non-skipped days in the past. The word "Behind" is a **technical term only** — it must never appear in user-facing UI copy. See also: *CatchUp*, *GraceFirst*.

---

**CatchUp**
The set of recovery options presented to a user who is Behind. CatchUp is always user-initiated (triggered by opening the app while Behind) and always offers the user a choice of strategy. It is never automatic and never punitive. The four strategies are: Compress (×4 window options), Skip & Continue, and Recalculate. See individual entries for each strategy.

---

**Compress**
A CatchUp strategy in which missed assignments are redistributed across a fixed future window (7, 14, or 30 days) or across all remaining plan days. The total Bible content covered by the plan is unchanged; the daily reading volume temporarily increases during the window. A Compress option is hidden if it would result in more than 5 chapters per day. The user must see the resulting daily load before confirming.

---

**DayIndex**
A 1-based integer representing a position in a reading plan. `DayIndex = 1` is the first day of the plan. DayIndex is the primary key for all progress records — it is plan-relative, not calendar-relative. This distinction matters for paused plans: when a plan is paused and resumed, calendar dates change but DayIndex values for completed days do not.

---

**hasCompletedOnboarding**
A device-local boolean stored in platform preferences (SharedPreferences on Android, UserDefaults on iOS) via a KMP `expect/actual` declaration in `AppPreferences`. Set to `true` exactly once, when the user reaches Step 8 of onboarding. Read once per cold start to route the user to onboarding or directly to the home screen. Never synced to Firestore, never reset by sign-out or account deletion. Scoped to the device, not to the account.

---

**GuestMode**
The app state in which a user has not created an account and has not signed in. All app features are fully available in GuestMode. Progress, bookmarks, and plan state are stored in local SQLDelight only — they are not synced to Firestore and are not recoverable if the app is deleted. GuestMode is a permanent valid state; the app never forces a user out of it or degrades functionality to pressure account creation.

---

**Plan**
A structured reading programme that assigns Bible passages to each calendar day over a fixed duration. In v1, there are four plan variants (2 durations × 2 reading orders): 1-year canon, 1-year chronological, 6-month canon, 6-month chronological. A plan is a static content asset shared by all users; it is not stored per-user. What is stored per-user is a *UserPlan* — the user's instance of a plan, with their start date, progress, and status.

---

**Recalculate**
A CatchUp strategy in which the plan's projected end date is extended by the number of unread days, making today the new "on track" day with no additional daily reading load. Historical unread days are marked as Skipped. The user sees the new end date before confirming. Recalculate is also the strategy applied automatically when a paused plan is resumed.

---

**Skip**
A day state (`DayState.SKIPPED`) applied when a user explicitly chooses to move past unread days without catching up, or when the Recalculate strategy is applied to historical gaps. A skipped day is distinct from a not-yet-read day and from a completed day. Skipped days are displayed in a neutral colour in the progress heatmap. They are never described as "missed" or "failed" in the UI. Skipped days are permanent — they cannot be retroactively marked complete.

---

**SyncPayload**
The set of user data fields that travel between the device and Firestore during a sync operation. The SyncPayload is deliberately minimal: `activePlanConfig`, `completedDays[]`, `skippedDays[]`, `pausedPlans[]`, `bookmarks[]`, and `notificationPrefs`. Bible text, plan assignment schedules, and reading order definitions are **never** part of the SyncPayload — they are bundled assets computed on-device. This keeps Firestore read/write costs low and makes the app functional offline indefinitely.

---

**UserPlan**
The per-user instance of a reading Plan. Stores the user's chosen plan type, reading order, start date, current day index, projected end date, status (ACTIVE / PAUSED / COMPLETED / ABANDONED), and optional mid-plan entry position. A user can have one ACTIVE UserPlan and multiple PAUSED or COMPLETED UserPlans at any time. UserPlan is part of the SyncPayload.

---

*End of SPEC.md v1.2*
