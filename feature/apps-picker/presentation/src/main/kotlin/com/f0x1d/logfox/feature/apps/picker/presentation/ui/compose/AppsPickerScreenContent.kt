package com.f0x1d.logfox.feature.apps.picker.presentation.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.component.button.NavigationBackButton
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.feature.apps.picker.api.InstalledApp
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerViewState
import com.f0x1d.logfox.feature.apps.picker.presentation.ui.AppsPickerScreenListener
import com.f0x1d.logfox.feature.apps.picker.presentation.ui.MockAppsPickerScreenListener
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
internal fun AppsPickerScreenContent(
    state: AppsPickerViewState,
    listener: AppsPickerScreenListener = MockAppsPickerScreenListener,
) {
    CompositionLocalProvider(
        LocalMultiplySelectionEnabled provides state.multiplySelectionEnabled,
    ) {
        val scrollBehavior = MiuixScrollBehavior()

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = state.topBarTitle,
                    navigationIcon = {
                        NavigationBackButton(onClick = listener.onBackClicked)
                    },
                    actions = {
                        IconButton(onClick = { listener.onSearchActiveChanged(true) }) {
                            Icon(imageVector = MiuixIcons.Search, contentDescription = null)
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
            ) {
                if (state.searchActive) {
                    InputField(
                        query = state.query,
                        onQueryChange = listener.onQueryChanged,
                        onSearch = { },
                        expanded = true,
                        onExpandedChange = { },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        label = stringResource(Strings.search),
                    )
                }

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        InfiniteProgressIndicator()
                    }
                } else {
                    AppsContent(
                        items = if (state.searchActive) state.searchedApps else state.apps,
                        checkedItems = state.checkedAppPackageNames,
                        listener = listener,
                        modifier = Modifier
                            .fillMaxSize()
                            .overScrollVertical()
                            .scrollEndHaptic()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = paddingValues.calculateBottomPadding(),
                        ),
                    )
                }
            }
        }

        BackHandler(
            enabled = state.searchActive,
            onBack = { listener.onSearchActiveChanged(false) },
        )
    }
}

@Composable
private fun AppsContent(
    items: List<InstalledApp>,
    checkedItems: Set<String>,
    listener: AppsPickerScreenListener,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        overscrollEffect = null,
    ) {
        item { Spacer(modifier = Modifier.size(12.dp)) }

        items(
            items = items,
            key = { item -> item.id },
        ) { item ->
            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
            ) {
                AppContent(
                    item = item,
                    isChecked = remember(checkedItems, item) {
                        item.packageName in checkedItems
                    },
                    onClick = listener.onAppClicked,
                    onChecked = listener.onAppChecked,
                )
            }
        }
    }
}

@Composable
internal fun AppContent(
    item: InstalledApp,
    isChecked: Boolean,
    onClick: (InstalledApp) -> Unit,
    onChecked: (InstalledApp, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicComponent(
        modifier = modifier,
        title = item.title,
        summary = item.packageName,
        startAction = {
            AsyncImage(
                modifier = Modifier.size(40.dp),
                model = item,
                contentDescription = null,
            )
        },
        endActions = if (LocalMultiplySelectionEnabled.current) {
            {
                Checkbox(
                    state = if (isChecked) ToggleableState.On else ToggleableState.Off,
                    onClick = { onChecked(item, !isChecked) },
                )
            }
        } else {
            null
        },
        onClick = { onClick(item) },
    )
}

internal val MockApps = listOf(
    InstalledApp("LogFox", "com.f0x1d.logfox"),
    InstalledApp("Sense", "com.f0x1d.sense"),
)
internal val MockAppsPickerState = AppsPickerViewState(
    topBarTitle = "Apps",
    apps = MockApps,
    checkedAppPackageNames = setOf(MockApps.first().packageName),
    searchedApps = MockApps,
    multiplySelectionEnabled = true,
    isLoading = false,
    searchActive = false,
    query = "",
)

@DayNightPreview
@Composable
private fun AppsPickerScreenContentPreview() = LogFoxTheme {
    AppsPickerScreenContent(
        state = MockAppsPickerState,
    )
}

@DayNightPreview
@Composable
private fun AppsPickerSearchScreenContentPreview() = LogFoxTheme {
    AppsPickerScreenContent(
        state = MockAppsPickerState.copy(searchActive = true),
    )
}

private val LocalMultiplySelectionEnabled = compositionLocalOf { false }
