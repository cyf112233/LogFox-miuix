package com.f0x1d.logfox.feature.crashes.presentation.details.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.compat.notificationsChannelsAvailable
import com.f0x1d.logfox.core.context.shareIntent
import com.f0x1d.logfox.core.copy.copyText
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsCommand
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsState
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsViewModel
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsViewState
import com.f0x1d.logfox.feature.crashes.presentation.details.ui.compose.CrashDetailsScreenContent
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class CrashDetailsFragment :
    BaseStoreComposeFragment<
        CrashDetailsViewState,
        CrashDetailsState,
        CrashDetailsCommand,
        CrashDetailsSideEffect,
        CrashDetailsViewModel,
        >() {

    override val viewModel by viewModels<CrashDetailsViewModel>()

    private val zipCrashLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) {
        it?.let { uri -> send(CrashDetailsCommand.ExportCrashToZip(uri)) }
    }

    // no plain because android will append .txt itself
    private val exportCrashLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        it?.let { uri -> send(CrashDetailsCommand.ExportCrashToFile(uri)) }
    }

    private var snackbarMessage by mutableStateOf<String?>(null)
    private var showConfirmBlacklist by mutableStateOf(false)
    private var showConfirmDelete by mutableStateOf(false)

    @Composable
    override fun Content(state: CrashDetailsViewState) = CrashDetailsScreenContent(
        state = state,
        notificationsChannelsAvailable = notificationsChannelsAvailable,
        snackbarMessage = snackbarMessage,
        onSnackbarShown = { snackbarMessage = null },
        confirmBlacklist = showConfirmBlacklist,
        onConfirmBlacklistDismiss = { showConfirmBlacklist = false },
        confirmDelete = showConfirmDelete,
        onConfirmDeleteDismiss = { showConfirmDelete = false },
        onBack = { findNavController().navigateUp() },
        onSearchQueryChange = { send(CrashDetailsCommand.SearchInLog(it)) },
        onToggleWrapLines = { send(CrashDetailsCommand.WrapLinesClicked) },
        onOpenAppInfo = { send(CrashDetailsCommand.OpenAppInfoClicked) },
        onOpenNotificationSettings = { send(CrashDetailsCommand.OpenNotificationSettingsClicked) },
        onBlacklistClick = { send(CrashDetailsCommand.BlacklistClicked) },
        onDeleteClick = { send(CrashDetailsCommand.DeleteClicked) },
        onConfirmBlacklist = {
            showConfirmBlacklist = false
            send(CrashDetailsCommand.ConfirmBlacklist)
        },
        onConfirmDelete = {
            showConfirmDelete = false
            send(CrashDetailsCommand.ConfirmDelete)
        },
        onCopyLog = { send(CrashDetailsCommand.CopyCrashLog) },
        onShareLog = { send(CrashDetailsCommand.ShareCrashLog) },
        onExportToFile = { send(CrashDetailsCommand.ExportCrashToFileClicked) },
        onExportToZip = { send(CrashDetailsCommand.ExportCrashToZipClicked) },
    )

    @SuppressLint("InlinedApi")
    override fun handleSideEffect(sideEffect: CrashDetailsSideEffect) {
        when (sideEffect) {
            is CrashDetailsSideEffect.OpenAppInfo -> {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", sideEffect.packageName, null)
                }.let(::startActivity)
            }

            is CrashDetailsSideEffect.OpenNotificationSettings -> {
                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, sideEffect.channelId)
                }.let(::startActivity)
            }

            is CrashDetailsSideEffect.ConfirmBlacklist -> {
                showConfirmBlacklist = true
            }

            is CrashDetailsSideEffect.ConfirmDelete -> {
                showConfirmDelete = true
            }

            is CrashDetailsSideEffect.CopyText -> {
                requireContext().copyText(sideEffect.text)
                snackbarMessage = ContextCompat.getString(requireContext(), Strings.text_copied)
            }

            is CrashDetailsSideEffect.ShareCrashLog -> {
                requireContext().shareIntent(sideEffect.text)
            }

            is CrashDetailsSideEffect.Close -> {
                findNavController().popBackStack()
            }

            is CrashDetailsSideEffect.LaunchFileExportPicker -> {
                exportCrashLauncher.launch(sideEffect.filename)
            }

            is CrashDetailsSideEffect.LaunchZipExportPicker -> {
                zipCrashLauncher.launch(sideEffect.filename)
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }
}
