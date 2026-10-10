package com.f0x1d.logfox.feature.crashes.presentation.appcrashes.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.designsystem.component.icon.AppIcon
import com.f0x1d.logfox.compose.designsystem.component.placeholder.ListPlaceholder
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesViewState
import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
internal fun AppCrashesScreenContent(
    state: AppCrashesViewState,
    onBack: () -> Unit,
    onCrashClick: (AppCrashesCountItem) -> Unit,
    onCrashDelete: (AppCrashesCountItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var crashToDelete by remember { mutableStateOf<AppCrashesCountItem?>(null) }
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = state.appName ?: state.packageName,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = MiuixIcons.Back, contentDescription = stringResource(Strings.back))
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        if (state.crashes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                ListPlaceholder(
                    iconResId = Icons.ic_bug,
                    text = {
                        Text(
                            text = stringResource(Strings.no_crashes),
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onBackgroundVariant,
                        )
                    },
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .overScrollVertical()
                    .scrollEndHaptic()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                ),
                overscrollEffect = null,
            ) {
                item { Spacer(modifier = Modifier.size(12.dp)) }

                items(state.crashes, key = { it.lastCrashId }) { item ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                    ) {
                        ArrowPreference(
                            title = item.crashType.readableName,
                            summary = item.formattedDate,
                            startAction = {
                                AppIcon(
                                    packageName = item.packageName,
                                    modifier = Modifier.size(40.dp),
                                )
                            },
                            endActions = {
                                IconButton(onClick = { crashToDelete = item }) {
                                    Icon(
                                        imageVector = MiuixIcons.Delete,
                                        contentDescription = stringResource(Strings.delete),
                                        tint = MiuixTheme.colorScheme.error,
                                    )
                                }
                            },
                            onClick = { onCrashClick(item) },
                        )
                    }
                }
            }
        }

        OverlayDialog(
            show = crashToDelete != null,
            title = stringResource(Strings.delete),
            summary = stringResource(Strings.delete_warning),
            onDismissRequest = { crashToDelete = null },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    text = stringResource(Strings.close),
                    onClick = { crashToDelete = null },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = stringResource(Strings.delete),
                    onClick = {
                        crashToDelete?.let(onCrashDelete)
                        crashToDelete = null
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }
    }
}
