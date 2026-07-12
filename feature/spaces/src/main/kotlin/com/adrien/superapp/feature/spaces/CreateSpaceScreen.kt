package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperPrimaryButton
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.SpaceVisibility

@Composable
fun CreateSpaceScreen(
    onBackClick: () -> Unit,
    onSpaceCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateSpaceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.createdSpaceId) {
        uiState.createdSpaceId?.let(onSpaceCreated)
    }

    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(
            title = "Nouvel espace",
            onBackClick = onBackClick,
            actions = {
                SuperPrimaryButton(
                    text = "Créer",
                    onClick = { viewModel.onAction(CreateSpaceAction.Submit) },
                    enabled = uiState.canSubmit,
                    loading = uiState.isSubmitting,
                    modifier = Modifier.padding(end = SuperAppTheme.spacing.space16),
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SuperAppTheme.spacing.screenHorizontal),
        ) {
            SuperTextField(
                value = uiState.name,
                onValueChange = { viewModel.onAction(CreateSpaceAction.NameChanged(it)) },
                label = "Nom",
                placeholder = "Équipe Design",
                singleLine = true,
            )

            SuperTextField(
                value = uiState.description,
                onValueChange = { viewModel.onAction(CreateSpaceAction.DescriptionChanged(it)) },
                label = "Description (facultative)",
                minLines = 2,
                modifier = Modifier.padding(top = SuperAppTheme.spacing.space16),
            )

            Text(
                text = "Visibilité",
                style = MaterialTheme.typography.titleMedium,
                color = SuperAppTheme.extendedColors.textPrimary,
                modifier = Modifier.padding(
                    top = SuperAppTheme.spacing.space24,
                    bottom = SuperAppTheme.spacing.space8,
                ),
            )
            SpaceVisibility.entries.forEach { visibility ->
                VisibilityRow(
                    visibility = visibility,
                    selected = uiState.visibility == visibility,
                    onSelect = { viewModel.onAction(CreateSpaceAction.VisibilityChanged(visibility)) },
                )
            }
        }
    }
}

@Composable
private fun VisibilityRow(
    visibility: SpaceVisibility,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val label = when (visibility) {
        SpaceVisibility.PRIVATE -> "Privé — vous seul(e)"
        SpaceVisibility.INVITE_ONLY -> "Sur invitation"
        SpaceVisibility.PUBLIC -> "Public"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = SuperAppTheme.extendedColors.textPrimary,
            modifier = Modifier.padding(start = SuperAppTheme.spacing.space8),
        )
    }
}
