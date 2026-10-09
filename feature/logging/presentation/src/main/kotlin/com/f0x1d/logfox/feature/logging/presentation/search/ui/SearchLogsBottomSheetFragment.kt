package com.f0x1d.logfox.feature.logging.presentation.search.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.f0x1d.logfox.core.ui.base.DynamicColorAvailabilityProvider
import com.f0x1d.logfox.core.ui.base.ext.enableEdgeToEdge
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsCommand
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsSideEffect
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsViewModel
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
 * The search bottom sheet stays a platform [BottomSheetDialogFragment] - so it keeps the native
 * sheet drag, dim and inset behaviour - but its whole content is now a Miuix composable.
 */
@AndroidEntryPoint
internal class SearchLogsBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<SearchLogsViewModel>()

    private val dynamicColorAvailabilityProvider: DynamicColorAvailabilityProvider by lazy {
        EntryPointAccessors
            .fromApplication<SearchLogsBottomSheetFragmentEntryPoint>(requireContext())
            .dynamicColorAvailabilityProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        consumeWindowInsets = false
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            LogFoxTheme(
                dynamicColor = dynamicColorAvailabilityProvider.isDynamicColorAvailable(),
            ) {
                val state by viewModel.state.collectAsStateWithLifecycle()
                SearchLogsScreenContent(
                    state = state,
                    send = viewModel::send,
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

    private fun handleSideEffect(sideEffect: SearchLogsSideEffect) {
        when (sideEffect) {
            is SearchLogsSideEffect.Dismiss -> dismiss()

            // Business logic side effects - handled by EffectHandler
            else -> Unit
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface SearchLogsBottomSheetFragmentEntryPoint {
        val dynamicColorAvailabilityProvider: DynamicColorAvailabilityProvider
    }
}
