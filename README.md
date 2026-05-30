# 📖 Rema — A Modern Bible Reader

> A Kotlin Multiplatform showcase app demonstrating production-grade mobile architecture across Android and iOS.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=flat&logo=kotlin)
![KMP](https://img.shields.io/badge/KMP-Stable-7F52FF?style=flat&logo=kotlin)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.6-4285F4?style=flat&logo=jetpackcompose)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green?style=flat)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI-blue?style=flat)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat)

---

## Why this project exists

This is not a tutorial app. It is a **deliberate architecture showcase** — built to demonstrate what a senior engineer considers production-quality cross-platform mobile code to look like in 2026.

The domain (a Bible reader) was chosen intentionally:

- **Offline-first** — real sync and caching challenges
- **Rich data model** — books, chapters, verses, translations, bookmarks, notes, reading plans
- **Search** — full-text search across large datasets
- **Multi-translation** — switching content sources at runtime
- **Personalisation** — user reading plans, highlights, history

These are the exact problems that reveal architecture quality. A weather app does not.

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| Multiplatform | Kotlin Multiplatform (KMP) — Stable |
| Android UI | Jetpack Compose |
| iOS UI | Compose Multiplatform |
| Architecture | Clean Architecture + MVI |
| Async | Coroutines + Flow |
| Networking | Ktor |
| Local storage | SQLDelight |
| Dependency injection | Koin (KMP-compatible) |
| Navigation | Compose Navigation |
| Testing | Kotlin Test + Turbine + MockK |
| Build | Gradle Version Catalogs + Convention Plugins |

---

## Architecture

The project follows **Clean Architecture** with strict layer separation and **MVI** (Model-View-Intent) for unidirectional data flow.

```
┌─────────────────────────────────────────────────┐
│                  Shared (KMP)                   │
│  ┌───────────┐  ┌───────────┐  ┌─────────────┐ │
│  │  domain   │←─│   data    │  │     di      │ │
│  │           │  │           │  │  (modules)  │ │
│  │  models   │  │  repos    │  └─────────────┘ │
│  │  usecases │  │  sources  │                  │
│  │  repo if. │  │  mappers  │                  │
│  └─────┬─────┘  └───────────┘                  │
│        │                                        │
│  ┌─────▼──────────────────────────────────────┐ │
│  │            presentation (ViewModels)        │ │
│  │      StateFlow<UiState> · Intent · MVI     │ │
│  └─────────────────────────────────────────────┘ │
└──────────────┬──────────────────┬────────────────┘
               │                  │
   ┌───────────▼───┐    ┌─────────▼──────────┐
   │  androidApp   │    │      iosApp         │
   │ Jetpack       │    │  Compose            │
   │ Compose UI    │    │  Multiplatform UI   │
   └───────────────┘    └────────────────────┘
```

### Key rules
- **Domain layer has zero Android/iOS imports** — pure Kotlin only
- **ViewModels live in shared code** — UI layers only observe state and dispatch intents
- **No logic in Composables** — screens are pure rendering functions
- **Repository interfaces in domain** — implementations in data

---

## Module structure

```
rema/
├── androidApp/                 # Android entry point
├── iosApp/                     # iOS entry point (Xcode project)
├── shared/
│   ├── domain/
│   │   ├── model/              # Bible, Book, Chapter, Verse, Bookmark...
│   │   ├── repository/         # interfaces (pure Kotlin)
│   │   └── usecase/            # GetChapterUseCase, SearchVerseUseCase...
│   ├── data/
│   │   ├── repository/         # implementations
│   │   ├── remote/             # Ktor API client
│   │   ├── local/              # SQLDelight DAOs
│   │   └── mapper/             # DTO ↔ Domain mappers
│   ├── presentation/
│   │   └── feature/
│   │       ├── reader/         # ReaderViewModel, ReaderUiState, ReaderIntent
│   │       ├── search/         # SearchViewModel...
│   │       ├── bookmarks/      # BookmarksViewModel...
│   │       └── settings/       # SettingsViewModel...
│   └── di/                     # Koin modules
├── build-logic/                # Convention plugins
└── gradle/libs.versions.toml   # Version catalog
```

---

## Features

### Implemented
- [ ] Display Bible chapters by book and chapter number
- [ ] Switch between translations at runtime
- [ ] Verse-level bookmarking
- [ ] Offline-first with SQLDelight cache
- [ ] Full-text verse search

### Planned
- [ ] Reading plans with daily progress tracking
- [ ] Verse highlighting with colour labels
- [ ] Reading history
- [ ] Cross-references between verses
- [ ] Audio playback integration

---

## MVI in practice

Every feature follows the same contract:

```kotlin
// State — immutable snapshot of what the UI should show
data class ReaderUiState(
    val book: Book? = null,
    val chapter: Chapter? = null,
    val verses: List<Verse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

// Intent — everything the user or system can trigger
sealed interface ReaderIntent {
    data class LoadChapter(val bookId: String, val chapter: Int) : ReaderIntent
    data class BookmarkVerse(val verseId: String) : ReaderIntent
    data class SwitchTranslation(val translationId: String) : ReaderIntent
}

// ViewModel — shared, platform-agnostic
class ReaderViewModel(
    private val getChapterUseCase: GetChapterUseCase,
    private val bookmarkVerseUseCase: BookmarkVerseUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ReaderIntent) {
        when (intent) {
            is ReaderIntent.LoadChapter -> loadChapter(intent.bookId, intent.chapter)
            is ReaderIntent.BookmarkVerse -> bookmarkVerse(intent.verseId)
            is ReaderIntent.SwitchTranslation -> switchTranslation(intent.translationId)
        }
    }
}
```

---

## Why KMP over Flutter or React Native

This project uses KMP intentionally, not by default. Here is the reasoning:

| Concern | KMP approach |
|---|---|
| Shared business logic | 100% shared — domain, data, ViewModels |
| Native UI | Each platform uses its own idiomatic UI toolkit |
| Performance | No bridge — compiles to native ARM on iOS |
| Kotlin ecosystem | Full access to Ktor, SQLDelight, Coroutines, Arrow |
| iOS interop | Direct Swift/ObjC interop without wrappers |
| Team fit | Android teams adopt KMP with near-zero ramp-up |

The tradeoff: UI code is written twice (Compose on Android, SwiftUI or Compose Multiplatform on iOS). The bet is that shared architecture + business logic is where the real cost lives — and KMP covers exactly that.

---

## What this project is designed to demonstrate

If you are a publisher, editor, or author evaluating my profile as a **technical book reviewer** for Kotlin, KMP, Android, or mobile architecture titles — this project is the practical proof of my claims.

Specifically it demonstrates:
- Ability to evaluate **KMP project structure** decisions (module boundaries, shared vs platform code)
- Understanding of **Clean Architecture enforcement** in a multiplatform context
- Familiarity with the **Koin vs Hilt** tradeoff in KMP projects
- Practical knowledge of **SQLDelight** as the KMP alternative to Room
- Real **MVI implementation** using StateFlow, not simplified pseudo-MVI
- Awareness of **Kotlin 2.0** changes affecting KMP (K2 compiler, stable multiplatform)

---

## Running the project

### Prerequisites
- Android Studio Hedgehog or later
- Xcode 15+ (for iOS target)
- JDK 17+

### Android
```bash
./gradlew :androidApp:installDebug
```

### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and run on simulator or device.

---

## Author

**Wilfried Mbouenda Mbogne** — Senior Android Engineer & Technical Book Reviewer  
14+ years of Android development · Droidcon Italy Hackathon winner · Speaker · Mentor

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=flat&logo=linkedin)](https://www.linkedin.com/in/mbouenda)
[![GitHub](https://img.shields.io/badge/GitHub-WillyShakes-181717?style=flat&logo=github)](https://github.com/WillyShakes)
