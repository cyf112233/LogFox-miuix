package com.f0x1d.logfox.compose.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back

@Composable
fun NavigationBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = MiuixIcons.Back,
            contentDescription = null,
        )
    }
}

@DayNightPreview
@Composable
private fun Preview() = LogFoxTheme {
    NavigationBackButton(onClick = { })
}
