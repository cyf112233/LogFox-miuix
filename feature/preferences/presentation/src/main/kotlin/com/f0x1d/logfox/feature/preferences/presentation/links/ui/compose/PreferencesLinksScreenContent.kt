package com.f0x1d.logfox.feature.preferences.presentation.links.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.preferences.presentation.SettingsScreen
import com.f0x1d.logfox.feature.preferences.presentation.settingsGroup
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.preference.ArrowPreference

private const val TELEGRAM_DEVELOPER_URL = "https://t.me/f0x1d"
private const val SOURCE_CODE_URL = "https://github.com/F0x1d/LogFox"
private const val RELEASES_URL = "https://t.me/f0x1dsshit"
private const val ALPHA_BUILDS_URL = "https://t.me/f0x1dsshit_ci"

@Composable
internal fun PreferencesLinksScreenContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    SettingsScreen(
        title = stringResource(Strings.links),
        modifier = modifier,
        onBack = onBack,
    ) {
        settingsGroup({ stringResource(Strings.about_app) }) {
            ArrowPreference(
                title = stringResource(Strings.developer),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_person),
                        contentDescription = null,
                    )
                },
                onClick = { uriHandler.openUriSafely(TELEGRAM_DEVELOPER_URL) },
            )
            ArrowPreference(
                title = stringResource(Strings.source_code),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_code),
                        contentDescription = null,
                    )
                },
                onClick = { uriHandler.openUriSafely(SOURCE_CODE_URL) },
            )
        }

        settingsGroup({ stringResource(Strings.telegram) }) {
            ArrowPreference(
                title = stringResource(Strings.releases),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_releases),
                        contentDescription = null,
                    )
                },
                onClick = { uriHandler.openUriSafely(RELEASES_URL) },
            )
            ArrowPreference(
                title = stringResource(Strings.alpha_builds),
                startAction = {
                    Icon(
                        painter = painterResource(Icons.ic_settings_handyman),
                        contentDescription = null,
                    )
                },
                onClick = { uriHandler.openUriSafely(ALPHA_BUILDS_URL) },
            )
        }
    }
}

private fun androidx.compose.ui.platform.UriHandler.openUriSafely(uri: String) {
    runCatching { openUri(uri) }
}
