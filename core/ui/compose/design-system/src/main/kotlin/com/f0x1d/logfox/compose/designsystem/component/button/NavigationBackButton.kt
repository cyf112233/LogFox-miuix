package com.f0x1d.logfox.compose.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.feature.strings.Strings
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
            contentDescription = stringResource(Strings.back),
        )
    }
}

@DayNightPreview
@Composable
private fun Preview() = LogFoxTheme {
    NavigationBackButton(onClick = { })
}
