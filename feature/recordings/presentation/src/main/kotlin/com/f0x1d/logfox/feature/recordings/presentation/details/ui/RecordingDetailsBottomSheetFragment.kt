package com.f0x1d.logfox.feature.recordings.presentation.details.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.core.context.shareFileIntent
import com.f0x1d.logfox.core.ui.base.ThemeSettingsProvider
import com.f0x1d.logfox.core.ui.base.ext.enableEdgeToEdge
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsCommand
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsSideEffect
import com.f0x1d.logfox.feature.recordings.presentation.details.RecordingDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch

/**
 * The recording details sheet stays a platform [BottomSheetDialogFragment] - so it keeps the native
 * sheet drag, dim and inset behaviour - but its whole content is now a Miuix composable.
 */
@AndroidEntryPoint
internal class RecordingDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<RecordingDetailsViewModel>()

    private val themeSettingsProvider: ThemeSettingsProvider by lazy {
        EntryPointAccessors
            .fromApplication<RecordingDetailsBottomSheetFragmentEntryPoint>(requireContext())
            .themeSettingsProvider
    }

    private val zipLogLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) {
        it?.let { uri -> viewModel.send(RecordingDetailsCommand.ExportZipFile(uri)) }
    }

    // no plain because android will append .txt itself
    private val logExportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        it?.let { uri -> viewModel.send(RecordingDetailsCommand.ExportFile(uri)) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        consumeWindowInsets = false
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            val monetEnabled by themeSettingsProvider.monetEnabled.collectAsStateWithLifecycle()

            LogFoxTheme(monetEnabled = monetEnabled) {
                val state by viewModel.state.collectAsStateWithLifecycle()
                RecordingDetailsScreenContent(
                    state = state,
                    onTitleChange = { viewModel.send(RecordingDetailsCommand.UpdateTitle(it)) },
                    onExportClick = { viewModel.send(RecordingDetailsCommand.ExportFileClicked) },
                    onShareClick = { viewModel.send(RecordingDetailsCommand.ShareRecording) },
                    onZipClick = { viewModel.send(RecordingDetailsCommand.ExportZipClicked) },
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffects.collect { sideEffect -> handleSideEffect(sideEffect) }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.enableEdgeToEdge(isContrastEnforced = false)
        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.disableShapeAnimations()
        return dialog
    }

    private fun handleSideEffect(sideEffect: RecordingDetailsSideEffect) {
        when (sideEffect) {
            is RecordingDetailsSideEffect.LaunchFileExportPicker -> {
                logExportLauncher.launch(sideEffect.filename)
            }

            is RecordingDetailsSideEffect.LaunchZipExportPicker -> {
                zipLogLauncher.launch(sideEffect.filename)
            }

            is RecordingDetailsSideEffect.ShareFile -> {
                requireContext().shareFileIntent(sideEffect.file)
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface RecordingDetailsBottomSheetFragmentEntryPoint {
        val themeSettingsProvider: ThemeSettingsProvider
    }
}
