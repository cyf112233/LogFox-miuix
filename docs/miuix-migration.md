# LogFox → Miuix migration contract

This file is the **single source of truth** for the migration. Follow it exactly; do not invent
your own layout numbers or APIs. Load the `miuix-ui` skill and read
`references/scenarios.md` + `references/preferences.md` before writing code.

Theme: `LogFoxTheme` (`core/ui/compose/design-system`) is the only theme entry point. It renders the
Miuix color schemes (`ColorSchemeMode.Light` / `Dark`) unless the user enabled Monet in the theme
settings — then `MonetLight` / `MonetDark`. **Monet is off by default** (`pref_monet_enabled = false`),
so out of the box the app uses the Miuix color schemes. `monetEnabled` is read from
`ThemeSettingsProvider` (a flow, so the switch takes effect immediately) and must never be read from
`SharedPreferences` inside a composable.

Library: **miuix 0.9.4** (`top.yukonga.miuix.kmp`), already added to `gradle/libs.versions.toml`
as the `miuix` bundle (`miuix-ui`, `miuix-preference`, `miuix-icons`) and applied automatically by
the `logfox.android.compose` / `logfox.android.feature.compose` convention plugins.

## 1. Hard rules

1. **No `androidx.compose.material3` imports at all.** `TopAppBar`, `Scaffold`, `Text`, `Card`,
   `Icon`, `Button`, `IconButton`, `HorizontalDivider`, `Switch`, `Checkbox` … all come from
   `top.yukonga.miuix.kmp.basic.*`. Preferences come from `top.yukonga.miuix.kmp.preference.*`,
   overlays from `top.yukonga.miuix.kmp.overlay.*`.
2. Colors only from `MiuixTheme.colorScheme.*`, text styles only from `MiuixTheme.textStyles.*`.
   No `Color(0xFF…)` literals, no `Color.Black`/`Color.White`.
3. Icons: `MiuixIcons.<Name>` from `top.yukonga.miuix.kmp.icon.extended.<Name>` (or
   `...icon.basic.<Name>`). **Each icon needs its own import** — they are extension properties.
4. Every page = `Scaffold` + `TopAppBar`/`SmallTopAppBar` (with `MiuixScrollBehavior`) + `LazyColumn`
   (`overScrollVertical()` + `scrollEndHaptic()` + `nestedScroll(behavior.nestedScrollConnection)` +
   `overscrollEffect = null`, `contentPadding` from `paddingValues.calculateTopPadding()`).
5. Grouped lists = `SmallTitle` + `Card(Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp))`
   + `XxxPreference` rows, dividers `HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))`.
   **Never** hand-roll `Row { Text(); Switch() }`.
6. Setting rows use `SwitchPreference` / `ArrowPreference` / `CheckboxPreference` /
   `RadioButtonPreference` / `SliderPreference` / `OverlayDropdownPreference` /
   `OverlaySpinnerPreference`. Simple buttons: `Button` / `TextButton`. Dialogs: `OverlayDialog`
   (must live inside the `Scaffold` content lambda, never inside a `LazyColumn` `item {}`).
7. Do not add `Modifier.statusBarsPadding()` on pages that have a `TopAppBar`.
8. Strings: `stringResource(Strings.xxx)`. No hardcoded user-facing text.
9. `LazyColumn` `items(...)` must pass a stable `key`.
10. Destructive actions need an `OverlayDialog` confirmation.

## 2. Architecture (unchanged)

Fragments stay the containers. ViewModels / TEA reducers / commands / ViewStates / DI / navigation
graphs **must not change**. Only the view layer is rewritten.

The base class is
`com.f0x1d.logfox.core.tea.BaseStoreComposeFragment<ViewState, State, Command, SideEffect, VM>`
(module `core/tea/android`). It hosts a `ComposeView`, wraps everything in `LogFoxTheme` (Miuix) and
collects `viewModel.state` with `collectAsStateWithLifecycle()`.

```kotlin
@AndroidEntryPoint
internal class XxxFragment : BaseStoreComposeFragment<
    XxxViewState, XxxState, XxxCommand, XxxSideEffect, XxxViewModel,
    >() {
    override val viewModel by viewModels<XxxViewModel>()

    @Composable
    override fun Content(state: XxxViewState) = XxxScreenContent(
        state = state,
        onBack = { findNavController().navigateUp() },
        onItemClick = { send(XxxCommand.Open(it.id)) },
    )

    override fun handleSideEffect(sideEffect: XxxSideEffect) { /* navigation only */ }
}
```

`Content` must be a **stateless** composable taking the ViewState + callbacks; put it in
`ui/compose/XxxScreenContent.kt` next to the fragment.

Screens that are plain (non-TEA) fragments may keep their existing structure but must render Miuix
components and be wrapped in `LogFoxTheme`.

## 3. Screen skeleton to copy

```kotlin
@Composable
fun XxxScreenContent(
    state: XxxViewState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = stringResource(Strings.xxx),
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(MiuixIcons.Back, contentDescription = null) }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            ),
            overscrollEffect = null,
        ) {
            item { Spacer(Modifier.size(12.dp)) }
            items(state.items, key = { it.id }) { item -> /* row */ }
        }
    }
}
```

Use `TopAppBar` (large, collapsible) for top-level tab pages and `SmallTopAppBar` for secondary pages.

## 4. Empty / loading states

Whole-page empty state: centred `Column` with `Icon(MiuixIcons.Xxx)`, a
`Text(..., color = MiuixTheme.colorScheme.onBackgroundVariant)` and, if the old screen had one, the
action button. Existing `ListPlaceholder` (`core/ui/compose/design-system`) is already Miuix — reuse it.

## 5. Module build files

* Switch the plugin to `alias(libs.plugins.logfox.android.feature.compose)` when the module gains
  Compose.
* Do **not** remove `buildFeatures.viewBinding = true` while any view-binding class in that module
  is still used; remove it in the final cleanup once the module has no `*Binding` usage.
* `core/tea/android` already exposes `core/ui/compose/designSystem` via `api`, so a module depending
  on `projects.core.tea.android` has the design system on its compile classpath.

## 6. Cleanup

* Delete XML layouts, menu resources and RecyclerView `Adapter`/`ViewHolder` classes that become
  unreferenced **inside the modules you own**.
* Keep `strings`/`plurals` resources — other screens still use them.
* Verify with `grep` that a deleted layout has no remaining `R.layout.` / binding references.

## 7. Definition of done for a screen

* No `material3` / `material.icons` imports in the files you touched.
* No hardcoded colors.
* Fragment is a container; composable is stateless.
* Layout follows §1.4/§1.5.
* Deleted files are actually deleted and nothing references them.
