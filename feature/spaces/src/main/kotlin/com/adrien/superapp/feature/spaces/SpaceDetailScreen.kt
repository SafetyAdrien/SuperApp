package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperFloatingActionButton
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun SpaceDetailScreen(
    onBackClick: () -> Unit,
    onPageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpaceDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.createdPageId) {
        uiState.createdPageId?.let(onPageClick)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SuperTopAppBar(title = uiState.space?.space?.name ?: "Espace", onBackClick = onBackClick)

            when {
                uiState.isLoading -> SuperLoadingIndicator()
                uiState.pages.isEmpty() -> SuperEmptyState(
                    title = "Aucune page pour l'instant",
                    subtitle = "Créez une première page pour cet espace.",
                    icon = Icons.AutoMirrored.Filled.Article,
                    modifier = Modifier.fillMaxSize(),
                )
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = uiState.pages, key = { it.id }) { page ->
                        PageListItem(page = page, onClick = { onPageClick(page.id) })
                    }
                }
            }
        }

        SuperFloatingActionButton(
            contentDescription = "Nouvelle page",
            onClick = viewModel::onCreatePageClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(SuperAppTheme.spacing.space16),
        )
    }
}
