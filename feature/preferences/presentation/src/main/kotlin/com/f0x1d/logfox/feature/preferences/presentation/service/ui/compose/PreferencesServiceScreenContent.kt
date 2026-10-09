package com.f0x1d.logfox.feature.preferences.presentation.service.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.feature.preferences.presentation.PreferenceDivider
import com.f0x1d.logfox.feature.preferences.presentation.SettingsScreen
import com.f0x1d.logfox.feature.preferences.presentation.rememberBooleanPreference
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceViewState
import com.f0x1d.logfox.feature.preferences.presentation.settingsGroup
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference

private const val KEY_FALLBACK_TO_DEFAULT_TERMINAL = "pref_fallback_to_default_terminal"
private const val KEY_START_ON_BOOT = "pref_start_on_boot"
private const val KEY_STOP_LOGGING_ON_BACK_EXIT = "pref_stop_logging_on_back_exit"
private const val KEY_SHOW_LOGS_FROM_APP_LAUNCH = "pref_show_logs_from_app_launch"
private const val KEY_INCLUDE_DEVICE_INFO = "pref_include_device_info_in_archives"
private const val KEY_INCLUDE_APP_INFO = "pref_include_app_info_in_exports"
private const val KEY_EXPORT_LOGS_AS_TXT = "pref_export_logs_as_txt"

@Composable
internal fun PreferencesServiceScreenContent(
    state: PreferencesServiceViewState,
    onTerminalSelected: (TerminalType) -> Unit,
    onStartOnBootChanged: (Boolean) -> Unit,
    onShowLogsFromAppLaunchChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    showRestartDialog: Boolean = false,
    onRestartDialogDismiss: () -> Unit = {},
    onRestartConfirmed: () -> Unit = {},
    showAndroid13Warning: Boolean = false,
    onAndroid13WarningDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        title = stringResource(Strings.service),
        modifier = modifier,
        onBack = onBack,
        overlays = {
            if (showRestartDialog) {
                OverlayDialog(
                    show = true,
                    title = stringResource(Strings.new_terminal_selected),
                    summary = stringResource(Strings.new_terminal_selected_question),
                    onDismissRequest = onRestartDialogDismiss,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TextButton(
                            text = stringResource(Strings.no),
                            onClick = onRestartDialogDismiss,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(
                            text = stringResource(Strings.yes),
                            onClick = onRestartConfirmed,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }

            if (showAndroid13Warning) {
                OverlayDialog(
                    show = true,
                    title = stringResource(Strings.warning),
                    summary = stringResource(Strings.android13_start_on_boot_warning),
                    onDismissRequest = onAndroid13WarningDismiss,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TextButton(
                            text = stringResource(android.R.string.ok),
                            onClick = onAndroid13WarningDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }
        },
    ) {
        settingsGroup({ stringResource(Strings.terminal) }) {
            OverlayDropdownPreference(
                title = stringResource(Strings.terminal),
                items = state.terminalNames,
                selectedIndex = TerminalType.entries.indexOf(state.selectedTerminalType),
                onSelectedIndexChange = { index ->
                    TerminalType.entries.getOrNull(index)?.let(onTerminalSelected)
                },
            )

            PreferenceDivider()

            val (fallback, setFallback) = rememberBooleanPreference(
                key = KEY_FALLBACK_TO_DEFAULT_TERMINAL,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.fallback_to_default_terminal),
                summary = stringResource(Strings.fallback_to_default_terminal_desc),
                checked = fallback,
                onCheckedChange = setFallback,
            )
        }

        settingsGroup({ stringResource(Strings.service) }) {
            val (startOnBoot, setStartOnBoot) = rememberBooleanPreference(
                key = KEY_START_ON_BOOT,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.start_on_boot),
                checked = startOnBoot,
                onCheckedChange = { checked ->
                    setStartOnBoot(checked)
                    onStartOnBootChanged(checked)
                },
            )

            PreferenceDivider()

            val (stopOnBackExit, setStopOnBackExit) = rememberBooleanPreference(
                key = KEY_STOP_LOGGING_ON_BACK_EXIT,
                defaultValue = false,
            )
            SwitchPreference(
                title = stringResource(Strings.stop_logging_on_back_exit),
                checked = stopOnBackExit,
                onCheckedChange = setStopOnBackExit,
            )

            PreferenceDivider()

            val (showFromLaunch, setShowFromLaunch) = rememberBooleanPreference(
                key = KEY_SHOW_LOGS_FROM_APP_LAUNCH,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.show_logs_from_app_launch),
                checked = showFromLaunch,
                onCheckedChange = { checked ->
                    setShowFromLaunch(checked)
                    onShowLogsFromAppLaunchChanged(checked)
                },
            )
        }

        settingsGroup({ stringResource(Strings.exports) }) {
            val (deviceInfo, setDeviceInfo) = rememberBooleanPreference(
                key = KEY_INCLUDE_DEVICE_INFO,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.include_device_information_in_archives),
                checked = deviceInfo,
                onCheckedChange = setDeviceInfo,
            )

            PreferenceDivider()

            val (appInfo, setAppInfo) = rememberBooleanPreference(
                key = KEY_INCLUDE_APP_INFO,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.include_app_information_in_exports),
                checked = appInfo,
                onCheckedChange = setAppInfo,
            )

            PreferenceDivider()

            val (exportAsTxt, setExportAsTxt) = rememberBooleanPreference(
                key = KEY_EXPORT_LOGS_AS_TXT,
                defaultValue = false,
            )
            SwitchPreference(
                title = stringResource(Strings.export_logs_as_txt),
                checked = exportAsTxt,
                onCheckedChange = setExportAsTxt,
            )
        }
    }
}
