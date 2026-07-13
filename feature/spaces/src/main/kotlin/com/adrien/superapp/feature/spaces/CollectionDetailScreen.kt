package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperBottomSheet
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperFloatingActionButton
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.CollectionViewType

private enum class CollectionSheet { PROPERTIES, CREATE_VIEW, VIEW_CONFIG }

@Composable
fun CollectionDetailScreen(
    onBackClick: () -> Unit,
    onEntryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectionDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeSheet by remember { mutableStateOf<CollectionSheet?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.createdEntryPageId) {
        uiState.createdEntryPageId?.let(onEntryClick)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SuperTopAppBar(
                title = uiState.collection?.title ?: "Base de données",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Options de la base de données")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Propriétés") },
                            onClick = { showMenu = false; activeSheet = CollectionSheet.PROPERTIES },
                        )
                        DropdownMenuItem(
                            text = { Text("Nouvelle vue") },
                            onClick = { showMenu = false; activeSheet = CollectionSheet.CREATE_VIEW },
                        )
                    }
                },
            )

            when {
                uiState.isLoading -> SuperLoadingIndicator()
                uiState.views.isEmpty() -> SuperEmptyState(
                    title = "Aucune vue",
                    subtitle = "Créez une vue pour afficher les entrées de cette base de données.",
                    icon = Icons.Filled.GridView,
                    modifier = Modifier.fillMaxSize(),
                )
                else -> {
                    val activeView = uiState.activeView
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TabRow(
                            selectedTabIndex = uiState.views.indexOfFirst { it.id == activeView?.id }.coerceAtLeast(0),
                            modifier = Modifier.weight(1f),
                        ) {
                            uiState.views.forEach { view ->
                                Tab(
                                    selected = view.id == activeView?.id,
                                    onClick = { viewModel.onSelectView(view.id) },
                                    text = { Text(view.name) },
                                )
                            }
                        }
                        IconButton(onClick = { activeSheet = CollectionSheet.VIEW_CONFIG }, enabled = activeView != null) {
                            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Configurer la vue")
                        }
                    }

                    if (activeView == null) {
                        SuperEmptyState(
                            title = "Aucune vue sélectionnée",
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else if (uiState.entries.isEmpty()) {
                        SuperEmptyState(
                            title = "Aucune entrée",
                            subtitle = "Ajoutez une première entrée à cette base de données.",
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        when (activeView.type) {
                            CollectionViewType.TABLE -> CollectionTableView(
                                entries = uiState.entries,
                                properties = uiState.properties,
                                onEntryClick = onEntryClick,
                                modifier = Modifier.fillMaxSize(),
                            )
                            CollectionViewType.LIST -> CollectionListView(
                                entries = uiState.entries,
                                onEntryClick = onEntryClick,
                                modifier = Modifier.fillMaxSize(),
                            )
                            CollectionViewType.KANBAN -> CollectionKanbanView(
                                entries = uiState.entries,
                                groupPropertyId = activeView.groupPropertyId,
                                onEntryClick = onEntryClick,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }

        SuperFloatingActionButton(
            contentDescription = "Nouvelle entrée",
            onClick = viewModel::onCreateEntry,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(SuperAppTheme.spacing.space16),
        )
    }

    when (activeSheet) {
        CollectionSheet.PROPERTIES -> SuperBottomSheet(onDismissRequest = { activeSheet = null }) {
            CollectionPropertiesSheetContent(
                properties = uiState.properties,
                optionsByProperty = uiState.optionsByProperty,
                onRename = viewModel::onRenameProperty,
                onToggleVisible = viewModel::onTogglePropertyVisible,
                onMove = viewModel::onMoveProperty,
                onDelete = viewModel::onDeleteProperty,
                onAddOption = viewModel::onAddOption,
                onDeleteOption = viewModel::onDeleteOption,
                onCreateProperty = viewModel::onCreateProperty,
            )
        }
        CollectionSheet.CREATE_VIEW -> SuperBottomSheet(onDismissRequest = { activeSheet = null }) {
            CreateViewContent(
                onCreate = { name, type ->
                    activeSheet = null
                    viewModel.onCreateView(name, type)
                },
            )
        }
        CollectionSheet.VIEW_CONFIG -> {
            val view = uiState.activeView
            if (view != null) {
                SuperBottomSheet(onDismissRequest = { activeSheet = null }) {
                    ViewConfigContent(
                        view = view,
                        properties = uiState.properties,
                        onUpdate = viewModel::onUpdateView,
                        onDelete = {
                            activeSheet = null
                            viewModel.onDeleteView(view.id)
                        },
                    )
                }
            }
        }
        null -> Unit
    }
}
