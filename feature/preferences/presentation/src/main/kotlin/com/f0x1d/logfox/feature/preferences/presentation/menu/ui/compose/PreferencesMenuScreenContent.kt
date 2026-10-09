package com.f0x1d.logfox.feature.preferences.presentation.menu.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.preferences.presentation.PreferenceDivider
import com.f0x1d.logfox.feature.preferences.presentation.SettingsScreen
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuViewState
import com.f0x1d.logfox.feature.preferences.presentation.settingsGroup
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.preference.ArrowPreference

@Composable
internal fun PreferencesMenuScreenContent(
    state: PreferencesMenuViewState,
    onUISettingsClick: () -> Unit,
    onServiceSettingsClick: () -> Unit,
    onCrashesSettingsClick: () -> Unit,
    onNotificationsSettingsClick: () -> Unit,
    onLinksClick: () -> Unit,
    onShareLogsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        title = stringResource(Strings.settings),
        modifier = modifier,
    ) {
        settingsGroup({ stringResource(Strings.settings) }) {
            ArrowPreference(
                title = stringResource(Strings.ui),
                startAction = {
                    Icon(painter = painterResource(Icons.ic_settings_ui), contentDescription = null)
                },
                onClick = onUISettingsClick,
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.service),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_service),
                        contentDescription = null,
                    )
                },
                onClick = onServiceSettingsClick,
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.crashes),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_crashes),
                        contentDescription = null,
                    )
                },
                onClick = onCrashesSettingsClick,
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.notifications),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_notifications),
                        contentDescription = null,
                    )
                },
                onClick = onNotificationsSettingsClick,
            )
        }

        settingsGroup({ stringResource(Strings.about_app) }) {
            ArrowPreference(
                title = stringResource(Strings.links),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_add_link),
                        contentDescription = null,
                    )
                },
                onClick = onLinksClick,
            )

            PreferenceDivider()

            BasicComponent(
                title = stringResource(Strings.about_app),
                summary = "${state.versionName} (${state.versionCode})",
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_info),
                        contentDescription = null,
                    )
                },
            )

            if (state.isDebug) {
                PreferenceDivider()

                ArrowPreference(
                    title = stringResource(Strings.share_logs),
                    onClick = onShareLogsClick,
                )
            }
        }
    }
}
