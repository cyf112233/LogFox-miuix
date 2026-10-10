package com.f0x1d.logfox.feature.setup.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.setup.presentation.SetupViewState
import com.f0x1d.logfox.feature.setup.presentation.ui.MockSetupScreenListener
import com.f0x1d.logfox.feature.setup.presentation.ui.SetupScreenListener
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * First run / permission screen: a hero, a short explanation and the three supported access
 * methods, grouped like every other Miuix list of choices.
 */
@Composable
internal fun SetupScreenContent(
    state: SetupViewState,
    listener: SetupScreenListener = MockSetupScreenListener,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = stringResource(id = Strings.setup),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                painter = painterResource(id = Icons.ic_logfox),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = Strings.setup),
                style = MiuixTheme.textStyles.title2,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = Strings.setup_description),
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onBackgroundVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                ArrowPreference(
                    title = stringResource(id = Strings.root),
                    startAction = {
                        Icon(
                            painter = painterResource(id = Icons.ic_square_root),
                            contentDescription = null,
                        )
                    },
                    onClick = listener.onRootClick,
                )

                SetupDivider()

                ArrowPreference(
                    title = stringResource(id = Strings.adb),
                    modifier = Modifier.testTag(SetupAdbButtonTestTag),
                    startAction = {
                        Icon(
                            painter = painterResource(id = Icons.ic_adb),
                            contentDescription = null,
                        )
                    },
                    onClick = listener.onAdbClick,
                )

                SetupDivider()

                ArrowPreference(
                    title = stringResource(id = Strings.shizuku),
                    startAction = {
                        Icon(
                            painter = painterResource(id = Icons.ic_terminal),
                            contentDescription = null,
                        )
                    },
                    onClick = listener.onShizukuClick,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = Strings.logs_restart_required),
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onBackgroundVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.showAdbDialog) {
            AdbDialog(
                message = stringResource(
                    id = Strings.how_to_use_adb,
                    state.adbCommand,
                ),
                onDismissed = listener.closeAdbDialog,
                checkPermission = listener.checkPermission,
                copyCommand = listener.copyCommand,
            )
        }
    }
}

/** Divider between two preference rows of the same group (16.dp so it lines up with the text). */
@Composable
private fun SetupDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}

@Composable
private fun AdbDialog(
    modifier: Modifier = Modifier,
    message: String = "",
    onDismissed: () -> Unit = { },
    checkPermission: () -> Unit = { },
    copyCommand: () -> Unit = { },
) {
    OverlayDialog(
        show = true,
        modifier = modifier.testTag(SetupAdbDialogTestTag),
        title = stringResource(id = Strings.adb),
        summary = message,
        onDismissRequest = onDismissed,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(
                text = stringResource(id = android.R.string.copy),
                onClick = {
                    copyCommand()
                    onDismissed()
                },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(id = Strings.check),
                onClick = {
                    checkPermission()
                    onDismissed()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
    }
}

const val SetupAdbButtonTestTag = "SetupAdbButton"
const val SetupAdbDialogTestTag = "SetupAdbDialog"

@DayNightPreview
@Composable
private fun SetupScreenContentPreview() = LogFoxTheme {
    SetupScreenContent(state = SetupViewState(showAdbDialog = false, adbCommand = ""))
}

@DayNightPreview
@Composable
private fun SetupScreenContentWithDialogPreview() = LogFoxTheme {
    SetupScreenContent(
        state = SetupViewState(
            showAdbDialog = true,
            adbCommand = "HESOYAM",
        ),
    )
}
