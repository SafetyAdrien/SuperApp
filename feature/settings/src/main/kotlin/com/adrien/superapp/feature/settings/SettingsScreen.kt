package com.adrien.superapp.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.common.ThemeMode
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(title = "Apparence", onBackClick = onBackClick)

        if (uiState.isLoading) {
            SuperLoadingIndicator()
            return
        }

        Text(
            text = "Thème",
            style = MaterialTheme.typography.titleMedium,
            color = SuperAppTheme.extendedColors.textPrimary,
            modifier = Modifier.padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space16,
            ),
        )
        ThemeMode.entries.forEach { mode ->
            ThemeModeRow(
                mode = mode,
                selected = uiState.themeMode == mode,
                onSelect = { onAction(SettingsAction.SetThemeMode(mode)) },
            )
        }

        SuperDivider(modifier = Modifier.padding(vertical = SuperAppTheme.spacing.space8))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                    vertical = SuperAppTheme.spacing.space16,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Couleurs dynamiques de l'appareil",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SuperAppTheme.extendedColors.textPrimary,
                )
                Text(
                    text = "Désactivez pour conserver l'identité visuelle de Super App.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SuperAppTheme.extendedColors.textSecondary,
                )
            }
            Switch(
                checked = uiState.useDynamicColor,
                onCheckedChange = { onAction(SettingsAction.SetUseDynamicColor(it)) },
            )
        }
    }
}

@Composable
private fun ThemeModeRow(
    mode: ThemeMode,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val label = when (mode) {
        ThemeMode.SYSTEM -> "Thème du système"
        ThemeMode.LIGHT -> "Clair"
        ThemeMode.DARK -> "Sombre"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space12,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = SuperAppTheme.extendedColors.textPrimary,
            modifier = Modifier.padding(start = SuperAppTheme.spacing.space12),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SettingsScreenPreview() {
    SuperAppTheme {
        SettingsScreen(
            uiState = SettingsUiState(themeMode = ThemeMode.SYSTEM, useDynamicColor = false, isLoading = false),
            onAction = {},
            onBackClick = {},
        )
    }
}
