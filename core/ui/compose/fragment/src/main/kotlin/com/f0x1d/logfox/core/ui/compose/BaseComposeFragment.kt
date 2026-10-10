package com.f0x1d.logfox.core.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.core.ui.base.ThemeSettingsProvider
import com.f0x1d.logfox.core.ui.base.fragment.BaseFragment
import com.f0x1d.logfox.core.ui.compose.databinding.FragmentComposeBinding
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

abstract class BaseComposeFragment : BaseFragment<FragmentComposeBinding>() {

    private val themeSettingsProvider: ThemeSettingsProvider by lazy {
        EntryPointAccessors
            .fromApplication<BaseComposeFragmentEntryPoint>(requireContext())
            .themeSettingsProvider
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentComposeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setup {
            val monetEnabled by themeSettingsProvider.monetEnabled.collectAsStateWithLifecycle()

            LogFoxTheme(monetEnabled = monetEnabled) {
                Content()
            }
        }
    }

    @Composable
    abstract fun Content()

    private fun ComposeView.setup(content: @Composable () -> Unit) {
        consumeWindowInsets = false
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent(content)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface BaseComposeFragmentEntryPoint {
        val themeSettingsProvider: ThemeSettingsProvider
    }
}
