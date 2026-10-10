package com.f0x1d.logfox.core.ui.base

import kotlinx.coroutines.flow.StateFlow

/**
 * Theme preferences the Compose theme entry point (`LogFoxTheme`, `core/ui/compose/design-system`)
 * needs.
 *
 * Everything is exposed as a [StateFlow] so a change made in the settings screen is reflected
 * immediately, instead of only after the activity was recreated.
 */
interface ThemeSettingsProvider {
    /**
     * Whether the Monet (wallpaper based) palette should be used instead of the Miuix color schemes.
     */
    val monetEnabled: StateFlow<Boolean>
}
