# AGENTS.md

## Project Overview

LogFox is an Android LogCat reader supporting Shizuku, root, and ADB access. It monitors logs, detects crashes (Java/JNI/ANR), records log sessions, and supports powerful filtering. Built with Miuix (Xiaomi HyperOS design language).

## Tech Stack

- Kotlin, Coroutines/Flow, Hilt (DI), Room (DB), Navigation Component (fragments)
- Compose + **Miuix 0.9.4** (`top.yukonga.miuix.kmp`) for all UI; Fragments are thin containers hosting a `ComposeView`
- Shizuku + libsu for privileged system access
- Roborazzi for snapshot testing

## Build Commands

Run ALL Gradle tasks with `--quiet` flag.

```bash
./gradlew :app:assembleDebug --quiet     # Build debug APK
./gradlew testDebugUnitTest --quiet      # Run unit tests
./gradlew recordRoborazziDebug --quiet   # Record golden snapshots
./gradlew verifyRoborazziDebug --quiet   # Run snapshot tests
```

## Architecture

### Conventions

- One top-level type per file; file name matches type name
- Use cases expose `operator fun invoke`; failable operations return `Result<T>` in use cases
- Hilt bindings return interfaces (`@Binds`), not implementation types

### Module Structure

The app uses clean architecture with multi-module organization:

```
feature/<name>/
  api/            # Domain interfaces, models, repository interfaces
  impl/           # Repository implementations, data sources (internal), DTOs (internal)
  presentation/   # ViewModel, Fragments/Composables, ViewState
```

**Dependency rules**:
- `presentation -> api` only (never import `impl`)
- `impl -> api` (its own module)
- Only `:app` aggregates `presentation` + `impl` modules

### UI Pattern

- **Container** (Fragment): owns ViewModel lifecycle, collects state/side effects, handles navigation
- **Passive views/composables**: render ViewState, expose callbacks, no business logic

#### Miuix conventions

All UI is Miuix — **never import `androidx.compose.material3`**. See `docs/miuix-migration.md` for the
full contract; the essentials:

- Theme: single entry point `LogFoxTheme` (`core/ui/compose/design-system`), which wraps `MiuixTheme`
  with a `ThemeController`. It renders Miuix' own color schemes by default; Monet (wallpaper) palettes
  are opt-in via `monetEnabled`, read reactively from `ThemeSettingsProvider`, never from
  `SharedPreferences` inside a composable. Never nest another theme.
- Colors/text styles only from `MiuixTheme.colorScheme` / `MiuixTheme.textStyles`.
- Icons: `MiuixIcons.<Name>` from `top.yukonga.miuix.kmp.icon.extended.<Name>` — each icon needs its own
  import (they are extension properties). Project-specific drawables go through `painterResource`.
- Page skeleton: `Scaffold` + `TopAppBar`/`SmallTopAppBar` (with `MiuixScrollBehavior`) + `LazyColumn`
  (`overScrollVertical()` + `scrollEndHaptic()` + `nestedScroll(behavior.nestedScrollConnection)`,
  `overscrollEffect = null`, `contentPadding` from `paddingValues.calculateTopPadding()`).
- Grouped lists/settings: `SmallTitle` + `Card(Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp))`
  + `XxxPreference` rows, with `HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))`
  between rows. Reuse `SettingsScreen` / `settingsGroup` / `PreferenceDivider` from
  `feature/preferences/presentation`.
- Dialogs/dropdowns (`Overlay*`) must live inside a `Scaffold` composition — pass them through a slot
  (e.g. `SettingsScreen(overlays = { … })`) rather than as a sibling of the screen composable.
- TEA screens extend `BaseStoreComposeFragment` (`core/tea/android`) and implement `Content(state)` plus
  `handleSideEffect(...)`; the stateless composable belongs in `ui/compose/XxxScreenContent.kt`.

## Gradle & Dependencies

- **Version catalog**: ALL dependencies and plugins accessed via `libs` (see `gradle/libs.versions.toml`)
- **Type-safe project accessors**: enabled via `TYPESAFE_PROJECT_ACCESSORS`
- **Convention plugins** in `build-logic/conventions/`:
  - `logfox.android.feature` — feature modules (Android Library + Hilt)
  - `logfox.android.feature.compose` — Compose-enabled feature modules
  - `logfox.android.library` — standard Android library modules
  - `logfox.kotlin.jvm` — pure Kotlin modules
  - `logfox.android.compose` — Compose configuration
  - `logfox.android.room` — Room database configuration
