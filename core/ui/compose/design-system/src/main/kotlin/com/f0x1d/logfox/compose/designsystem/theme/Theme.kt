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
 * @param darkTheme whether the dark color scheme should be used
 * @param dynamicColor whether the Monet (wallpaper based) palette should be used.
 *   Callers must only pass `true` when the platform actually supports dynamic colors.
 */
@Composable
fun LogFoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val controller = remember(darkTheme, dynamicColor) {
        ThemeController(
            colorSchemeMode = when {
                dynamicColor && darkTheme -> ColorSchemeMode.MonetDark
                dynamicColor -> ColorSchemeMode.MonetLight
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
