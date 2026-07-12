package com.adrien.superapp.feature.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun PageDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PageDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(title = "Page", onBackClick = onBackClick)

        when {
            uiState.isLoading -> SuperLoadingIndicator()
            uiState.notFound -> SuperEmptyState(
                title = "Page introuvable",
                subtitle = "Elle a peut-être été supprimée.",
                modifier = Modifier.fillMaxSize(),
            )
            else -> Column(modifier = Modifier.fillMaxSize()) {
                SuperTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChanged,
                    singleLine = true,
                    modifier = Modifier.padding(SuperAppTheme.spacing.screenHorizontal),
                )
                SuperEmptyState(
                    title = "L'éditeur de blocs arrive bientôt",
                    subtitle = "Paragraphes, titres, listes, cases à cocher et plus — dans une prochaine phase.",
                    icon = Icons.AutoMirrored.Filled.Article,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
