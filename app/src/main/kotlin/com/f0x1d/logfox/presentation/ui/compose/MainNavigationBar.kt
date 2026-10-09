package com.f0x1d.logfox.presentation.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.f0x1d.logfox.feature.navigation.api.Directions
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Notes
import top.yukonga.miuix.kmp.icon.extended.RecordingTape
import top.yukonga.miuix.kmp.icon.extended.Report
import top.yukonga.miuix.kmp.icon.extended.Settings

internal data class MainDestination(
    val id: Int,
    val labelRes: Int,
    val icon: ImageVector,
)

@Composable
internal fun MainNavigationBar(
    currentDestinationId: Int,
    landscape: Boolean,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val destinations = remember {
        listOf(
            MainDestination(Directions.logs, Strings.app_name, MiuixIcons.Notes),
            MainDestination(Directions.crashes, Strings.crashes, MiuixIcons.Report),
            MainDestination(Directions.recordings, Strings.recordings, MiuixIcons.RecordingTape),
            MainDestination(Directions.settings, Strings.settings, MiuixIcons.Settings),
        )
    }

    if (landscape) {
        NavigationRail(modifier = modifier) {
            destinations.forEach { destination ->
                NavigationRailItem(
                    selected = currentDestinationId == destination.id,
                    onClick = { onSelect(destination.id) },
                    icon = destination.icon,
                    label = stringResource(destination.labelRes),
                )
            }
        }
    } else {
        NavigationBar(modifier = modifier) {
            destinations.forEach { destination ->
                NavigationBarItem(
                    selected = currentDestinationId == destination.id,
                    onClick = { onSelect(destination.id) },
                    icon = destination.icon,
                    label = stringResource(destination.labelRes),
                )
            }
        }
    }
}
