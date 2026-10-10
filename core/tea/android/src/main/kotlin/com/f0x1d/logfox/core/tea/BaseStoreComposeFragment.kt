package com.f0x1d.logfox.core.tea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.core.ui.base.ThemeSettingsProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch

/**
 * Compose counterpart of [BaseStoreFragment].
 *
 * The fragment stays the container: it owns the ViewModel lifecycle, collects side effects and
 * navigates. The UI itself is a stateless composable rendered by [Content].
 */
abstract class BaseStoreComposeFragment<
    ViewState,
    State,
    Command,
    SideEffect,
    VM : BaseStoreViewModel<ViewState, State, Command, SideEffect>,
    > : Fragment() {

    protected abstract val viewModel: VM

    /**
     * Stateless content of this screen. Recomposition friendly: same state -> same UI.
     */
    @Composable
    protected abstract fun Content(state: ViewState)

    /**
     * Handle side effects (navigation, snackbars, ...). Business logic side effects are handled by
     * the effect handlers.
     */
    protected abstract fun handleSideEffect(sideEffect: SideEffect)

    /**
     * Called after the view was created and before side effects start being collected.
     */
    protected open fun onContentViewCreated(view: View, savedInstanceState: Bundle?) = Unit

    private val themeSettingsProvider: ThemeSettingsProvider by lazy {
        EntryPointAccessors
            .fromApplication<BaseStoreComposeFragmentEntryPoint>(requireContext())
            .themeSettingsProvider
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
                Content(state)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onContentViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffects.collect { sideEffect -> handleSideEffect(sideEffect) }
            }
        }
    }

    /**
     * Convenience method to send commands to ViewModel.
     */
    protected fun send(command: Command) {
        viewModel.send(command)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface BaseStoreComposeFragmentEntryPoint {
        val themeSettingsProvider: ThemeSettingsProvider
    }
}
