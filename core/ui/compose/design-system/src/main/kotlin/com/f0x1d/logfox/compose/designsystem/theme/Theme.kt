package com.f0x1d.logfox.compose.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

/**
 * Single theme entry point of the app. Everything visual comes from [MiuixTheme].
 *
 * By default the app renders with the Miuix color schemes ([ColorSchemeMode.Light] /
 * [ColorSchemeMode.Dark]); Monet (wallpaper based) palettes are opt-in through the theme settings
 * and only then the `MonetLight` / `MonetDark` modes are used.
 *
 * @param darkTheme whether the dark color scheme should be used
 * @param monetEnabled whether the Monet (wallpaper based) palette should be used. Callers are
 *   responsible for only passing `true` when the platform supports dynamic colors.
 */
@Composable
fun LogFoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    monetEnabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val controller = remember(darkTheme, monetEnabled) {
        ThemeController(
            colorSchemeMode = when {
                monetEnabled && darkTheme -> ColorSchemeMode.MonetDark
                monetEnabled -> ColorSchemeMode.MonetLight
                darkTheme -> ColorSchemeMode.Dark
                else -> ColorSchemeMode.Light
            },
        )
    }

    MiuixTheme(
        controller = controller,
        content = content,
    )
}
