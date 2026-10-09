package com.f0x1d.logfox.feature.filters.presentation.edit.ui

import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterCommand
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterSideEffect
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterState
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewModel
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewState
import com.f0x1d.logfox.feature.filters.presentation.edit.ui.compose.EditFilterScreenContent
import com.f0x1d.logfox.feature.navigation.api.Directions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class EditFilterFragment :
    BaseStoreComposeFragment<
        EditFilterViewState,
        EditFilterState,
        EditFilterCommand,
        EditFilterSideEffect,
        EditFilterViewModel,
        >() {

    override val viewModel by hiltNavGraphViewModels<EditFilterViewModel>(
        Directions.editFilterFragment,
    )

    private val exportFilterLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri?.let { send(EditFilterCommand.Export(it)) }
    }

    private var showDiscardDialog by mutableStateOf(false)

    @Composable
    override fun Content(state: EditFilterViewState) {
        // Routes system/predictive back through TEA while the form has unsaved changes.
        BackHandler(enabled = state.isDirty) {
            send(EditFilterCommand.AttemptClose)
        }

        EditFilterScreenContent(
            state = state,
            onClose = { send(EditFilterCommand.AttemptClose) },
            onSave = { send(EditFilterCommand.Save) },
            onRename = { send(EditFilterCommand.UpdateName(it)) },
            onExport = { exportFilterLauncher.launch("filter.json") },
            onToggleIncluding = { send(EditFilterCommand.ToggleIncluding) },
            onToggleEnabled = { send(EditFilterCommand.ToggleEnabled) },
            onToggleLogLevel = { which, checked ->
                send(EditFilterCommand.FilterLevel(which, checked))
            },
            onSelectApp = { send(EditFilterCommand.SelectApp) },
            onUidChange = { send(EditFilterCommand.UpdateUid(it)) },
            onPidChange = { send(EditFilterCommand.UpdatePid(it)) },
            onTidChange = { send(EditFilterCommand.UpdateTid(it)) },
            onPackageNameChange = { send(EditFilterCommand.UpdatePackageName(it)) },
            onTagChange = { send(EditFilterCommand.UpdateTag(it)) },
            onContentChange = { send(EditFilterCommand.UpdateContent(it)) },
            confirmDiscard = showDiscardDialog,
            onConfirmDiscardDismiss = { showDiscardDialog = false },
            onConfirmDiscard = {
                showDiscardDialog = false
                send(EditFilterCommand.AttemptCloseConfirmed)
            },
        )
    }

    override fun handleSideEffect(sideEffect: EditFilterSideEffect) {
        when (sideEffect) {
            is EditFilterSideEffect.NavigateToAppPicker -> {
                findNavController().navigate(Directions.action_editFilterFragment_to_appsPickerFragment)
            }

            is EditFilterSideEffect.Close -> {
                findNavController().popBackStack()
            }

            is EditFilterSideEffect.ConfirmDiscard -> {
                showDiscardDialog = true
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }
}
