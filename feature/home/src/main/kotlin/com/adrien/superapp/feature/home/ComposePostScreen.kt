package com.adrien.superapp.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperPrimaryButton
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun ComposePostScreen(
    onBackClick: () -> Unit,
    onPostCreated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComposePostViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.didSubmit) {
        if (uiState.didSubmit) onPostCreated()
    }

    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(
            title = if (uiState.replyToPostId == null) "Nouvelle publication" else "Répondre",
            onBackClick = onBackClick,
            actions = {
                SuperPrimaryButton(
                    text = "Publier",
                    onClick = { viewModel.onAction(ComposePostAction.Submit) },
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
                value = uiState.text,
                onValueChange = { viewModel.onAction(ComposePostAction.TextChanged(it)) },
                placeholder = "Quoi de neuf ?",
                minLines = 4,
                isError = uiState.remainingCharacters < 0,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SuperAppTheme.spacing.space8),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "${uiState.remainingCharacters}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (uiState.remainingCharacters < 0) {
                        SuperAppTheme.extendedColors.error
                    } else {
                        SuperAppTheme.extendedColors.textSecondary
                    },
                )
            }
        }
    }
}
