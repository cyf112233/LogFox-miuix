package com.f0x1d.logfox.feature.recordings.presentation.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.compose.designsystem.component.button.RichButton
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsViewState
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Stateless content of the recording details bottom sheet: the recording date, an editable title and
 * the export / share / zip actions.
 *
 * It is rendered inside the platform bottom sheet dialog, which provides the surface and the window
 * insets - so this intentionally has no `Scaffold`.
 */
@Composable
internal fun RecordingDetailsScreenContent(
    state: RecordingDetailsViewState,
    onTitleChange: (String) -> Unit,
    onExportClick: () -> Unit,
    onShareClick: () -> Unit,
    onZipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Seeded once from the loaded state; afterwards the field is the source of truth, so the
    // round-trip through the store (which echoes every keystroke back) cannot reset the cursor.
    var title by remember(state.currentTitle) { mutableStateOf(state.currentTitle.orEmpty()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        state.recordingItem?.let { recordingItem ->
            Text(
                text = recordingItem.formattedDate,
                style = MiuixTheme.textStyles.title3,
            )
        }

        TextField(
            value = title,
            onValueChange = {
                title = it
                onTitleChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Strings.title),
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RichButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(text = stringResource(Strings.export)) },
                    icon = {
                        Icon(
                            painter = painterResource(Icons.ic_export),
                            contentDescription = null,
                        )
                    },
                    onClick = onExportClick,
                )

                RichButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(text = stringResource(Strings.share)) },
                    icon = {
                        Icon(
                            painter = painterResource(Icons.ic_share),
                            contentDescription = null,
                        )
                    },
                    onClick = onShareClick,
                )

                RichButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(text = stringResource(Strings.zip)) },
                    icon = {
                        Icon(
                            painter = painterResource(Icons.ic_archive),
                            contentDescription = null,
                        )
                    },
                    onClick = onZipClick,
                )
            }
        }
    }
}
