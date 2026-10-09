package com.f0x1d.logfox.feature.preferences.presentation.notifications.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.preferences.presentation.PreferenceDivider
import com.f0x1d.logfox.feature.preferences.presentation.SettingsScreen
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsViewState
import com.f0x1d.logfox.feature.preferences.presentation.rememberBooleanPreference
import com.f0x1d.logfox.feature.preferences.presentation.settingsGroup
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val KEY_SEPARATE_CHANNELS = "pref_notifications_use_separate_channels"
private const val KEY_NOTIFICATIONS_JAVA = "pref_notifications_java"
private const val KEY_NOTIFICATIONS_JNI = "pref_notifications_jni"
private const val KEY_NOTIFICATIONS_ANR = "pref_notifications_anr"

@Composable
internal fun PreferencesNotificationsScreenContent(
    state: PreferencesNotificationsViewState,
    onOpenLoggingNotificationSettings: () -> Unit,
    onOpenNotificationsPermissionSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        title = stringResource(Strings.notifications),
        modifier = modifier,
        onBack = onBack,
    ) {
        if (!state.hasNotificationsPermission) {
            item {
                BasicComponent(
                    title = stringResource(Strings.no_notification_permission),
                    summary = stringResource(Strings.notification_permission_is_required),
                    titleColor = top.yukonga.miuix.kmp.basic.BasicComponentDefaults.titleColor(
                        color = MiuixTheme.colorScheme.error,
                    ),
                    startAction = {
                        Icon(
                            painter = painterResource(Icons.ic_dialog_notification_important),
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.error,
                        )
                    },
                    onClick = onOpenNotificationsPermissionSettings,
                )
            }
        }

        if (state.notificationsChannelsAvailable) {
            settingsGroup({ stringResource(Strings.logging) }) {
                ArrowPreference(
                    title = stringResource(Strings.logging_notification),
                    onClick = onOpenLoggingNotificationSettings,
                )
            }
        }

        settingsGroup({ stringResource(Strings.crashes) }) {
            if (state.notificationsChannelsAvailable) {
                BasicComponent(
                    title = stringResource(Strings.crashes),
                    summary = stringResource(Strings.per_app_notifications_settings),
                )

                PreferenceDivider()
            }

            val (separateChannels, setSeparateChannels) = rememberBooleanPreference(
                key = KEY_SEPARATE_CHANNELS,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.use_separate_channels_for_crashes),
                checked = separateChannels,
                onCheckedChange = setSeparateChannels,
            )

            PreferenceDivider()

            val (java, setJava) = rememberBooleanPreference(KEY_NOTIFICATIONS_JAVA, true)
            SwitchPreference(
                title = stringResource(Strings.show_java_crashes_notifications),
                checked = java,
                onCheckedChange = setJava,
            )

            PreferenceDivider()

            val (jni, setJni) = rememberBooleanPreference(KEY_NOTIFICATIONS_JNI, true)
            SwitchPreference(
                title = stringResource(Strings.show_jni_crashes_notifications),
                checked = jni,
                onCheckedChange = setJni,
            )

            PreferenceDivider()

            val (anr, setAnr) = rememberBooleanPreference(KEY_NOTIFICATIONS_ANR, true)
            SwitchPreference(
                title = stringResource(Strings.show_anr_notifications),
                checked = anr,
                onCheckedChange = setAnr,
            )
        }
    }
}
