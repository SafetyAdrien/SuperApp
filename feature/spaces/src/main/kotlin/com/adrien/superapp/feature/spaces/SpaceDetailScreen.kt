package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperBottomSheet
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperFloatingActionButton
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun SpaceDetailScreen(
    onBackClick: () -> Unit,
    onPageClick: (String) -> Unit,
    onCollectionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpaceDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.createdPageId) {
        uiState.createdPageId?.let(onPageClick)
    }
    LaunchedEffect(uiState.createdCollectionId) {
        uiState.createdCollectionId?.let(onCollectionClick)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SuperTopAppBar(title = uiState.space?.space?.name ?: "Espace", onBackClick = onBackClick)

            when {
                uiState.isLoading -> SuperLoadingIndicator()
                uiState.pages.isEmpty() && uiState.collections.isEmpty() -> SuperEmptyState(
                    title = "Cet espace est vide",
                    subtitle = "Créez une page ou une base de données pour commencer.",
                    icon = Icons.AutoMirrored.Filled.Article,
                    modifier = Modifier.fillMaxSize(),
                )
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (uiState.pages.isNotEmpty()) {
                        item {
                            SectionLabel("Pages")
                        }
                        items(items = uiState.pages, key = { "page-${it.id}" }) { page ->
                            PageListItem(page = page, onClick = { onPageClick(page.id) })
                        }
                    }
                    if (uiState.collections.isNotEmpty()) {
                        item {
                            SectionLabel("Bases de données")
                        }
                        items(items = uiState.collections, key = { "collection-${it.id}" }) { collection ->
                            CollectionListItem(collection = collection, onClick = { onCollectionClick(collection.id) })
                        }
                    }
                }
            }
        }

        SuperFloatingActionButton(
            contentDescription = "Créer",
            onClick = { showCreateSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(SuperAppTheme.spacing.space16),
        )
    }

    if (showCreateSheet) {
        SuperBottomSheet(onDismissRequest = { showCreateSheet = false }) {
            Column {
                CreateOptionRow(
                    label = "Nouvelle page",
                    icon = Icons.AutoMirrored.Filled.Article,
                    onClick = {
                        showCreateSheet = false
                        viewModel.onCreatePageClick()
                    },
                )
                CreateOptionRow(
                    label = "Nouvelle base de données",
                    icon = Icons.Filled.GridView,
                    onClick = {
                        showCreateSheet = false
                        viewModel.onCreateCollectionClick()
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = SuperAppTheme.extendedColors.textSecondary,
        modifier = modifier.padding(
            horizontal = SuperAppTheme.spacing.screenHorizontal,
            vertical = SuperAppTheme.spacing.space12,
        ),
    )
}

@Composable
private fun CreateOptionRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = label, onClick = onClick)
            .padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space16,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SuperAppTheme.extendedColors.textSecondary)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = SuperAppTheme.extendedColors.textPrimary,
            modifier = Modifier.padding(start = SuperAppTheme.spacing.space16),
        )
    }
}
