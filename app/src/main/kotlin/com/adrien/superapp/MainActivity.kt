package com.adrien.superapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        var uiState: AppUiState by mutableStateOf(AppUiState())

        installSplashScreen().setKeepOnScreenCondition { uiState.isLoading }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState = it }
            }
        }

        enableEdgeToEdge()

        setContent {
            SuperAppTheme(
                themeMode = uiState.themeMode,
                useDynamicColor = uiState.useDynamicColor,
            ) {
                MainScreen(isOnline = uiState.isOnline)
            }
        }
    }
}
