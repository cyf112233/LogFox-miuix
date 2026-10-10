# Miuix 复查报告（严格对照 skill + docs/miuix-migration.md）

复查范围：`app`、`core/ui`、`core/tea/android`、`feature/*/presentation` 的全部 UI 代码与资源。
复查方式：逐文件人工对照 `miuix-ui` skill（§1 十四条铁律、`references/scenarios.md`、`theme.md`、`preferences.md`、`scenario-*.md`）+ 项目自己的迁移契约。
结论：**lint 全绿（0 error / 7 warning）不代表合规**。lint 只能查 import、字面量色、滚动修饰符；本次复查发现的偏差集中在
「非 Compose 壳」「主题链路」「绕过 TEA 的偏好读写」「观感细节」，lint 一条都查不出来。

严重度：**P0** 功能/一致性缺陷 · **P1** 规范硬性违反 · **P2** 观感与一致性

---

## P0-1 Monet 开关是「假开关」，且底栏与页面配色不一致

* `feature/preferences/presentation/.../ui/settings/ui/compose/PreferencesUIScreenContent.kt:36,111-119`
  直接读写 `SharedPreferences("pref_monet_enabled")`（`rememberBooleanPreference`），与
  `UISettingsLocalDataSourceImpl:22-30` 的仓库键重复定义。
* `Theme.kt:23-32` 的 `LogFoxTheme` **从不读这个偏好**；它只接受调用方传的 `dynamicColor`。
* 真正的值来自 `app/.../di/DynamicColorAvailabilityProviderModule.kt:19`
  `isDynamicColorAvailable() = uiSettingsRepository.monetEnabled().value` —— 一次性的
  `StateFlow.value` 快照（非响应式），因此只有 `RecreateActivity` 副作用重建 Activity 后才生效。
* `app/.../MainActivity.kt:165` 的底栏 `LogFoxTheme { }` **没有传 `dynamicColor`**（默认 `false`）
  → 底栏永远用 Miuix 静态色，页面用 Monet 色，同一屏两套配色。
* `LogFoxApp.kt:42-48` 又调了 Material 的 `DynamicColors.applyToActivitiesIfAvailable`
  （Material Monet）作用在 XML/Material 主题上 → 第三个配色来源。

**修法**：Monet 由仓库单一来源提供 → `UISettingsRepository.monetEnabled()` 的 Flow 贯通
`LogFoxTheme`（响应式，不依赖 Activity 重建）；UI 用 `SwitchPreference(checked = state.monetEnabled)`；
删除 Material `DynamicColors`；`Theme.kt` 里把「明暗」和「是否 Monet」收敛成 `ColorSchemeMode`
（`System/Light/Dark/MonetSystem/MonetLight/MonetDark`），关掉 Monet 时明确走 Miuix 自带色系。

## P0-2 主题设置页的分组与语义混乱

* `PreferencesUIScreenContent.kt:95-121`：第一组标题是 `Strings.ui`（"界面"），组里却塞了
  夜间模式 + Monet + 日期格式 + 时间格式；主题项没有独立的「主题」分组。
* `PreferencesUIReducer.kt`：`NightThemeChanged` 直接把下拉 index 当作 AppCompat 常量
  （0/1/2 == FOLLOW_SYSTEM/NO/YES）——数值恰好相等，属巧合式正确。
* `PreferencesUIScreenContent.kt:36` `KEY_NIGHT_THEME` 是死常量；`44-49` 的
  `LOGS_*_DEFAULT`/`DATE_FORMAT_DEFAULT` 与 repository 常量重复。

## P0-3 两个底部弹层仍是 Material `BottomSheetDialogFragment`

* `feature/logging/.../search/ui/SearchLogsBottomSheetFragment.kt:24-26,80-88`
* `feature/recordings/.../details/ui/RecordingDetailsBottomSheetFragment.kt:26-28,98-106`
  都用 `com.google.android.material.bottomsheet.*` + `BottomSheetBehavior`。
* 两者内容（`SearchLogsScreenContent.kt:41-47`、`RecordingDetailsScreenContent.kt:30-36`）在注释里
  明确写「intentionally has no `Scaffold`」，因此也无法使用 `OverlayBottomSheet`，
  且 `Column + verticalScroll` 代替了规范要求的 `LazyColumn`。

## P0-4 通知权限弹窗仍是 Material 对话框

* `MainActivity.kt:124-141` `MaterialAlertDialogBuilder`。规范：弹窗用 `OverlayDialog`，
  且必须活在某个 `Scaffold` 内。

## P0-5 大量设置在 Composable 里直接读写 SharedPreferences，绕过 TEA/仓库

新增的 `PreferencesState.kt`（`rememberBooleanPreference` / `rememberIntPreference` /
`rememberLongPreference` / `rememberStringPreference`）被 4 个屏直接使用：

| 文件 | 处数 | 键 |
| :-- | --: | :-- |
| `service/ui/compose/PreferencesServiceScreenContent.kt` | 5 | `pref_fallback_to_default_terminal` … |
| `notifications/ui/compose/PreferencesNotificationsScreenContent.kt` | 4 | `pref_notifications_*` |
| `crashes/ui/compose/PreferencesCrashesScreenContent.kt` | 3 | `pref_collect_*` |
| `ui/settings/ui/compose/PreferencesUIScreenContent.kt` | 6 | `pref_open_crashes_page_on_startup` … |

后果：`ViewState` 形同虚设（`PreferencesCrashesScreenContent` 干脆不接收 state），
UI 与仓库成为两个事实来源，键名/默认值在 UI 层重复一份。绑定关系也不完整
（`PreferencesUIFragment` 只把 `onStartOnBootChanged` 之类少数回调回灌仓库）。

## P0-6 XML / View 壳仍然是主框架

* `app/src/main/res/layout/activity_main.xml`、`activity_main_no_bar.xml`：
  `ConstraintLayout` + `FragmentContainerView` + `ComposeView`，底栏布局与显隐靠
  `MainActivity.kt:65-85,227-244` 的两套 `ConstraintSet` + `ChangeBounds` 切换。
* `core/ui/compose/fragment/src/main/res/layout/fragment_compose.xml`：`BaseComposeFragment`
  用 ViewBinding 承载 `ComposeView`（`BaseComposeFragment.kt:14,28,32`）。
* `core/ui/theme/src/main/res/values/themes.xml`：父主题 `Theme.Material3.DayNight.NoActionBar`，
  外加 `materialAlertDialogTheme` / `bottomSheetDialogTheme` / `materialCardViewOutlinedStyle`
  和整套 `md_theme_*` 颜色。
* 导航仍是 5 个 XML 图 + Safe Args（`feature/navigation/api/src/main/res/navigation/*.xml`），
  底部弹层是 `<dialog>` 目的地（`logs.xml:39-42`）。

## P0-7 已无调用点的 Material/旧 View 残留

`SnackbarExt.kt`（Material `Snackbar`）、`FABExt.kt`（Material FAB）、
`CustomApplyInsetsNavigationRailView.kt`（Material `NavigationRailView`）全项目 0 处引用；
`drawable/bg_toolbar_title_clickable.xml`、`color/item_log_background_ripple.xml` 等旧 View 资源同样无引用。

---

## P1-1 每个 Fragment 各套一层 `LogFoxTheme`，且明暗来源不统一

`Theme.kt` 用 `isSystemInDarkTheme()` + AppCompat `setDefaultNightMode`
（`LogFoxApp.kt:41`、`PreferencesUIEffectHandler` 的 `SaveNightTheme`），而 skill `theme.md`
要求的接线方式是 `ThemeController(ColorSchemeMode.…)`。两套明暗状态并存（AppCompat uiMode 与
Compose 主题），Monet 与明暗被拆成两条互不相关的链路。

## P1-2 手搓交互与硬编码视觉值（违反铁律 1/10）

* `LogsScreenContent.kt:398-403` 两个 `pointerInput { detectTapGestures }` 手搓点击/长按，
  没有按下态与涟漪；选中底色 `390-395` 也不走 `pressable`。
* `LogsScreenContent.kt:503-508` `RoundedCornerShape` 徽章；skill `effects.md` 要求 squircle
  （`squircleSurface` / `squircleClip`）。
* `RecordingsScreenContent.kt:228` `fontSize = 17.sp, fontWeight = SemiBold` 手写文本样式
  （应用 `MiuixTheme.textStyles.main/title4`）。
* `RecordingsScreenContent.kt:128` 空态 `padding(vertical = 20.dp)`、`SetupScreenContent.kt:62`
  `24.dp`、`LogLineRow` 的 `3.dp` 等手写常量表。
* `FiltersScreenContent.kt:119`、`AppCrashesScreenContent.kt:77` 空态用 `Modifier.padding(paddingValues)`
  全量内边距（规范只允许 `calculateTopPadding()`）。

## P1-3 无障碍：图标按钮的 `contentDescription`

返回/更多/搜索/删除类图标大量 `contentDescription = null`：
`SettingsScreen.kt:51`、`FiltersScreenContent.kt:79`、`AppCrashesScreenContent.kt:66`、
`CrashDetailsScreenContent.kt:119,124,164`、`EditFilterScreenContent.kt:92,97,115`、
`AppsPickerScreenContent.kt:73`；`LogsScreenContent.kt:223` 的「更多」用了 `Strings.app_name` 当描述。

## P1-4 `OverlayDialog` 内的单选/多选组没有 `Card` 容器

`CrashesScreenContent.kt:246-271`（SortDialog）、`EditFilterScreenContent.kt:288-295`、
`PreferencesUIScreenContent.kt:341-348`。`preferences.md` 的单选组标准写法是
`Card { RadioButtonPreference … }`。

## P1-5 弹层/对话框组件在 5 个文件里各写一遍

`AnchorDropdownMenu`（`LogsScreenContent.kt:437-479`、`RecordingsScreenContent.kt:259-299`）、
`ConfirmationDialog`（`RecordingsScreenContent.kt:303-335`）、`ConfirmButtons`
（`CrashesScreenContent.kt:296-319`）、`ConfirmRow`（`CrashDetailsScreenContent.kt:362-385`）、
`DialogButtons`（`PreferencesUIScreenContent.kt:451-473`）。应下沉到 design-system。

## P1-6 搜索交互三套写法

`CrashesScreenContent.kt:114-122` 用项目 `TopSearchBar`；`AppsPickerScreenContent.kt:86-94`
与 `CrashDetailsScreenContent.kt:177-187` 用裸 `InputField`，后者的 `onExpandedChange = { }`、
`onSearch = { }` 是空实现；三者都放在 `Column` 里与列表并列（规范推荐 `SearchBar` 进
`TopAppBar` 或作为唯一滚动容器的外层）。

## P1-7 主题内没有 Miuix 自己的色系入口

skill `theme.md` 的 `ThemePaletteStyle`（`TonalSpot`/`Neutral`/`Vibrant`/`Expressive`）与
`ThemeColorSpec`（`Spec2021`/`Spec2025`）全项目未使用；关闭 Monet 后只有一套固定默认色，
与「miuix 自己有色系」的诉求不符。

---

## P2-1 其余观感/一致性问题

* `RecordingsScreenContent.kt:102-121` 首行没有 `Spacer(12.dp)` 呼吸位；空态塞在 `LazyColumn` 的
  `item {}` 里（`124-142`），不是整页居中状态页。
* `LogsExtendedCopyScreenContent.kt:40` 用已废弃的 `LocalClipboardManager`（新 API 为 `LocalClipboard`）。
* `SearchLogsScreenContent.kt:54,89-98` 输入框内容与 store 两个事实来源，清除按钮只看 `state.query`。
* `AppsPickerScreenContent.kt:232` `compositionLocalOf` 未加 `static`；无多选计数/完成操作，
  `BackHandler`（`124-127`）与 `InputField` 的空回调并存。
* `SetupScreenContent.kt` 是引导页，但没有按 `scenario-onboarding.md` 的结构（无插画/步骤/权限四态），
  只是三个按钮居中。
* `PreferencesMenuScreenContent.kt:32` 用 `Strings.settings` 同时当页面标题与分组标题，分组信息丢失。
* 预览：`RecordingsScreenContent`、`AppsPickerScreenContent`、`SetupScreenContent` 有
  `@DayNightPreview`，但大多数屏（`LogsScreenContent`、`CrashesScreenContent`、各 preferences 屏）
  没有；`core/tests/screenshot` 模块存在却**没有任何截图测试**，`recordRoborazziDebug` 实际不产出金标。

---

## 修复批次（据此执行）

1. **主题层**：仓库单一来源的 Monet + `ColorSchemeMode` 收敛 + 主题分组重构 + 删 Material DynamicColors。
2. **全 Compose 壳**：`MainActivity` → `setContent` + Miuix `Scaffold` + `NavigationBar`；删两份 activity XML；
   `BaseComposeFragment` 去掉 ViewBinding；`MaterialAlertDialogBuilder` → `OverlayDialog`。
3. **弹层 Compose 化**：两个 `BottomSheetDialogFragment` → Miuix `OverlayBottomSheet`。
4. **偏好链路**：删掉 `PreferencesState.kt`，全部改为 state/command/side-effect 驱动。
5. **组件下沉**：`AnchorDropdownMenu`/`ConfirmDialog`/`DialogButtons` 进 design-system。
6. **观感细节**：squircle、`pressable`/`combinedClickable`、文本样式 token、首行呼吸位、
   空态整页居中、a11y 描述、`Card` 包单选组。
7. **验证**：编译 + 单测 + 为关键屏补 Roborazzi 截图测试并人工核对截图。

---

## 修复进展

| 批次 | 状态 | 提交 |
| :-- | :-- | :-- |
| 1. 主题层（P0-1 / P0-2 / P1-7 的接线部分） | ✅ 已完成 | `feat(theme): make the Monet switch real and put it in its own Theme group` |
| 6. 观感细节（P1-2 / P1-3 / P1-4 + 搜索统一 P1-6） | ✅ 已完成 | `polish(ui): bring the screens closer to the Miuix reference look` |
| 2. 全 Compose 壳（P0-6 / P0-4） | ⏳ 进行中 | 需 `navigation-compose`（已用 `javap` 核对 Miuix 侧 API、用 AndroidX 源码核对 `composable`/`navigation`/`dialog` 签名） |
| 3. 弹层 Compose 化（P0-3） | ⏳ 待办 | 依赖批次 2（改由父屏或 `dialog()` 目的地承载 `OverlayBottomSheet`） |
| 4. 偏好链路 state 化（P0-5） | ⏳ 待办 | 仓库键与 UI 层键同名，需把 4 个屏接回 ViewState |
| 5. 组件下沉（P1-5） | ⏳ 待办 | `AnchorDropdownMenu` / `ConfirmDialog` / `DialogButtons` → design-system |

**验证方式的变化**：构建机上没有 Android SDK 也没有算力跑 Gradle，所以本地不执行编译；
改由 GitHub Actions 构建，代码侧用 `javap` 反查 Miuix `0.9.4` 真实签名、
用 AndroidX 源码核对 `navigation-compose` 签名来替代编译器。

### 已完成项的落地细节

* **Monet**：`UISettingsRepository.monetEnabled()` 是唯一来源；
  `ThemeSettingsProvider`（`core/ui/base`）以 `StateFlow` 暴露给 `LogFoxTheme`；
  设置页由 `PreferencesUIViewState.monetEnabled` 驱动，切换即时生效（不再重建 Activity）；
  删除了 Material 的 `DynamicColors`；底栏与页面共用同一份 Monet 状态。
* **观感**：日志行改用 `combinedClickable`（有按压反馈）+ `squircleSurface` 徽章；
  录像行/过滤器行改用 `BasicComponent`（去掉手写 17sp/SemiBold 文本样式与 `ArrowPreference` 误用）；
  排序/日志等级/日志格式三个弹窗的选择组包进 `Card`；崩溃详情与应用选择页统一用 `TopSearchBar`；
  所有纯图标按钮补 `contentDescription`（新增 `Strings.back` / `Strings.more`）。

### 仍未处理（下一批）

* `SetupScreenContent` 仍是「三个按钮居中」，没有按 `scenario-onboarding.md` 的引导页结构重做。
* 空态（crashes / filters / appCrashes）用 `Modifier.padding(paddingValues)` 全量内边距。
* `core/tests/screenshot` 模块没有任何截图测试，`recordRoborazziDebug` 不产出金标。
* `AnchorDropdownMenu` / `ConfirmDialog` / `DialogButtons` 仍在 5 个文件里重复。

