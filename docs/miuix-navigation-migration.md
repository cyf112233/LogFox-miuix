# 全 Compose 导航迁移方案（P0-3 / P0-4 / P0-6）

目标：删掉所有 XML 布局、Fragment 容器、Material 组件与 XML 导航图，把 UI 变成纯 Compose + Miuix，
导航改用 `androidx.navigation:navigation-compose`（用户选定方案 B）。

已核对的事实（不靠记忆）：

* `composable(route: String, arguments: List<NamedNavArgument>, deepLinks, content: @Composable (NavBackStackEntry) -> Unit)`
* `navigation(startDestination: String, route: String, arguments, deepLinks, …, builder: NavGraphBuilder.() -> Unit)`
* `dialog(route: String, arguments, deepLinks, dialogProperties, content)`
  —— 三个签名取自 AndroidX 源码 `navigation-compose/src/commonMain/.../NavGraphBuilder.kt`。
* Miuix 侧签名用 `javap` 反查缓存里的 `0.9.4` aar：`squircleSurface(color, topStart, topEnd, bottomEnd, bottomStart)`、
  `Modifier.pressable(interactionSource, indication, enabled, role, delay)`（`interactionSource` 无默认值）、
  `BasicComponent(modifier, title, titleColor, summary, summaryColor, startAction, endActions, bottomAction, insideMargin, onClick, …)`。
* `miuix-squircle` 在 `miuix-ui` 的 POM 里是 `compile` scope，所以特性模块可以直接 import `top.yukonga.miuix.kmp.squircle.*`。

## 1. 依赖

`gradle/libs.versions.toml`

```toml
androidx-navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "androidx-navigation" }
androidx-hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version.ref = "androidx-hilt-navigation-fragment" }
```

* `core/tea/android`：`api(libs.androidx.navigation.compose)` + `api(libs.androidx.hilt.navigation.compose)`
  （所有 presentation 模块都依赖它，一处生效）。
* `:app`：显式加这两个依赖；删掉 `libs.bundles.androidx.navigation`、`libs.androidx.hilt.navigation.fragment`、
  `libs.material`（弹层与对话框全部 Miuix 化之后）。
* `feature/navigation/api`：删掉 `res/navigation/*.xml` 与 `Navigation.kt`（`Directions`/`NavGraphs` 两个 typealias
  本质是 `R.id`/`R.navigation`，XML 一删就失效），改为纯 Kotlin 的 `Routes.kt`。

## 2. 路由表（保留各 Tab 独立返回栈）

```
NavHost(startDestination = logs_graph)
├─ navigation(startDestination = "logs",        route = "logs_graph")
│    ├─ "logs"                                 LogsRoute
│    ├─ "logs/extended_copy?content={content}" LogsExtendedCopyRoute
│    ├─ "logs/filters"                         FiltersRoute
│    ├─ "logs/filters/edit?filter_id=&log_uid=&…" EditFilterRoute
│    ├─ "apps_picker"                          AppsPickerRoute
│    └─ dialog("logs/search")                  SearchLogsRoute（Miuix OverlayBottomSheet）
├─ navigation(startDestination = "crashes",     route = "crashes_graph")
│    ├─ "crashes"                              CrashesRoute
│    ├─ "crashes/app?package_name=&app_name="  AppCrashesRoute
│    └─ "crashes/details?crash_id="            CrashDetailsRoute
├─ navigation(startDestination = "recordings",  route = "recordings_graph")
│    ├─ "recordings"                           RecordingsRoute
│    └─ dialog("recordings/details/{recording_id}") RecordingDetailsRoute
├─ navigation(startDestination = "settings",    route = "settings_graph")
│    ├─ "settings" / "settings/ui" / "settings/service" /
│    │   "settings/notifications" / "settings/crashes" / "settings/links"
└─ "setup"                                     SetupRoute（全局，popUpTo(graph){inclusive}）
```

参数键沿用现有 snake_case（`crash_id` / `recording_id` / `package_name` / `app_name` / `filter_id` /
`log_*`），这样 4 个 `SavedStateHandle` DI 模块（`CrashDetailsViewModelModule` 等）**一行都不用改**。

## 3. 壳

`MainActivity : AppCompatActivity`（`setContent`，`enableEdgeToEdge()`，ViewPump 的 `attachBaseContext` 保留），
`MainScreen()`：

```kotlin
Scaffold(
    bottomBar = { if (barShown) MainNavigationBar(...) },   // Miuix NavigationBar / 横屏 NavigationRail
    topBar = { },                                            // 各页面自带顶栏
) { padding ->
    NavHost(..., modifier = Modifier.padding(padding)) { … }
}
```

* 底栏显隐：`currentBackStackEntryAsState()` 的 route ∈ 隐藏集合（setup / 所有二级页）。
* 返回：`BackHandler(enabled = route in 四个 Tab 根)` → `MainCommand.BackPressedAtRoot`（与旧
  `handleOnBackPressed` 语义一致）。
* 通知权限弹窗：`MaterialAlertDialogBuilder` → `OverlayDialog`。
* 底栏 `MainNavigationBar` 改用 `navigate(Routes.X_GRAPH) { popUpTo(graph.startDestinationId){saveState=true};
  launchSingleTop=true; restoreState=true }`，保持「每个 Tab 各自返回栈」。

## 4. 容器 → 目的地

`core/tea/android` 新增：

```kotlin
@Composable
fun <ViewState, SideEffect, VM> StoreRoute(
    viewModel: VM,
    onSideEffect: (SideEffect) -> Unit,
    content: @Composable (ViewState) -> Unit,
) where VM : BaseStoreViewModel<ViewState, *, *, SideEffect>
```

每个 `XxxFragment` 变成 `XxxRoute(vm = hiltViewModel())`：收集 `state`、把 `handleSideEffect` 里的导航
换成 `navController` 调用，其余原样传给已有的 `XxxScreenContent`（这些文件本轮已经润色过，不再改动）。

## 5. 为什么两个底部弹层用 `dialog()`

`RecordingDetailsViewModel` 的 `@RecordingId` 来自 `SavedStateHandle`。`hiltViewModel()` 没有 `extras` 参数，
在父屏里没法规避；把弹层做成 `dialog("recordings/details/{recording_id}")` 目的地后，
参数的落点与旧 `dialog` 导航图完全一致，DI 模块不用动。内容用 `Scaffold { OverlayBottomSheet(...) }`
（`Overlay*` 需要 composition 内有 `Scaffold`；`dialog()` 的目的地正好可以放一个）。

## 6. 删除清单

`app/src/main/res/layout/activity_main{,_no_bar}.xml`、`core/ui/compose/fragment/`（`BaseComposeFragment` +
`fragment_compose.xml`）、`core/tea/android/BaseStoreFragment.kt`、`core/ui/base/activity/BaseActivity.kt`、
`core/ui/base/fragment/BaseFragment.kt`、`core/ui/base/ext/SnackbarExt.kt`、`core/ui/view/`（`FABExt`、
`CustomApplyInsetsNavigationRailView` 等 0 引用）、19 个 `*Fragment.kt`、5 个导航 XML、
`feature/navigation/api/Navigation.kt`。

## 7. 验证

```bash
./gradlew :app:compileDebugKotlin --quiet                    # Kotlin 检查（用户允许的唯一 Gradle 用途）
./gradlew :feature:setup:presentation:compileDebugUnitTestKotlin --quiet
```

迁移完成后仍需 `./gradlew recordRoborazziDebug --quiet` 重录 Setup 金标。
