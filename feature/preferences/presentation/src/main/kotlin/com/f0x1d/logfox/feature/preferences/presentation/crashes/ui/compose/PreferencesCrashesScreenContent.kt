package com.f0x1d.logfox.feature.preferences.presentation.crashes.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.f0x1d.logfox.feature.preferences.presentation.PreferenceDivider
import com.f0x1d.logfox.feature.preferences.presentation.SettingsScreen
import com.f0x1d.logfox.feature.preferences.presentation.rememberBooleanPreference
import com.f0x1d.logfox.feature.preferences.presentation.settingsGroup
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.preference.SwitchPreference

private const val KEY_COLLECT_JAVA = "pref_collect_java"
private const val KEY_COLLECT_JNI = "pref_collect_jni"
private const val KEY_COLLECT_ANR = "pref_collect_anr"

@Composable
internal fun PreferencesCrashesScreenContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        title = stringResource(Strings.crashes),
        modifier = modifier,
        onBack = onBack,
    ) {
        settingsGroup({ stringResource(Strings.crashes) }) {
            val (collectJava, setCollectJava) = rememberBooleanPreference(KEY_COLLECT_JAVA, true)
            SwitchPreference(
                title = stringResource(Strings.collect_java_crashes),
                checked = collectJava,
                onCheckedChange = setCollectJava,
            )

            PreferenceDivider()

            val (collectJni, setCollectJni) = rememberBooleanPreference(KEY_COLLECT_JNI, true)
            SwitchPreference(
                title = stringResource(Strings.collect_jni_crashes),
                checked = collectJni,
                onCheckedChange = setCollectJni,
            )

            PreferenceDivider()

            val (collectAnr, setCollectAnr) = rememberBooleanPreference(KEY_COLLECT_ANR, true)
            SwitchPreference(
                title = stringResource(Strings.collect_anr),
                checked = collectAnr,
                onCheckedChange = setCollectAnr,
            )
        }
    }
}
