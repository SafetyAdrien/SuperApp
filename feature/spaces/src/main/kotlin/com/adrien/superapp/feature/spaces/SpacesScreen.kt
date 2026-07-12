package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import kotlinx.coroutines.launch

private val DefaultExpandedSections = setOf("recent", "spaces")

/**
 * The "Espaces" tab's home — the workspace-home concept from Notion Mobile: collapsible sections
 * (Récents, Espaces, ...) rather than a flat list. "Favoris"/"Pages privées"/"Pages partagées"/
 * "Modèles"/"Corbeille" are structurally present but have no backing feature yet (no favorite
 * flag, no sharing, no templates, no soft-delete UI) — each renders an explicit placeholder row
 * instead of pretending to be empty-because-unused.
 */
@Composable
fun SpacesScreen(
    onSpaceClick: (String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onPageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpacesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Not rememberSaveable: a Set<String> has no default Bundle saver, and losing the expand/
    // collapse state across process death is a minor cosmetic nit, not worth a custom Saver.
    var expandedSections by remember { mutableStateOf(DefaultExpandedSections) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun toggle(section: String) {
        expandedSections = if (section in expandedSections) {
            expandedSections - section
        } else {
            expandedSections + section
        }
    }

    fun notAvailableYet(label: String) {
        coroutineScope.launch { snackbarHostState.showSnackbar("$label — bientôt disponible") }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SuperTopAppBar(
                title = "Espaces",
                actions = {
                    IconButton(onClick = { notAvailableYet("Recherche") }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Rechercher")
                    }
                    IconButton(onClick = { notAvailableYet("Notifications") }) {
                        Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                },
            )

            if (uiState.isLoading) {
                SuperLoadingIndicator()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        WorkspaceSectionHeader(
                            title = "Récents",
                            expanded = "recent" in expandedSections,
                            onToggle = { toggle("recent") },
                        )
                    }
                    if ("recent" in expandedSections) {
                        if (uiState.recentPages.isEmpty()) {
                            item { EmptySectionRow("Aucune page récente") }
                        } else {
                            items(items = uiState.recentPages, key = { "recent-${it.id}" }) { page ->
                                PageListItem(page = page, onClick = { onPageClick(page.id) })
                            }
                        }
                    }

                    item {
                        WorkspaceSectionHeader(
                            title = "Favoris",
                            expanded = "favorites" in expandedSections,
                            onToggle = { toggle("favorites") },
                        )
                    }
                    if ("favorites" in expandedSections) {
                        item { EmptySectionRow("Bientôt disponible") }
                    }

                    item {
                        WorkspaceSectionHeader(
                            title = "Espaces",
                            expanded = "spaces" in expandedSections,
                            onToggle = { toggle("spaces") },
                            trailingAction = {
                                IconButton(onClick = onCreateSpaceClick) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Nouvel espace")
                                }
                            },
                        )
                    }
                    if ("spaces" in expandedSections) {
                        if (uiState.spaces.isEmpty()) {
                            item { EmptySectionRow("Créez un espace pour réunir pages, projets et discussions.") }
                        } else {
                            items(items = uiState.spaces, key = { it.space.id }) { spaceWithStats ->
                                SpaceListItem(
                                    spaceWithStats = spaceWithStats,
                                    onClick = { onSpaceClick(spaceWithStats.space.id) },
                                )
                            }
                        }
                    }

                    item {
                        WorkspaceSectionHeader(
                            title = "Pages privées",
                            expanded = "private" in expandedSections,
                            onToggle = { toggle("private") },
                        )
                    }
                    if ("private" in expandedSections) {
                        item { EmptySectionRow("Bientôt disponible") }
                    }

                    item {
                        WorkspaceSectionHeader(
                            title = "Pages partagées",
                            expanded = "shared" in expandedSections,
                            onToggle = { toggle("shared") },
                        )
                    }
                    if ("shared" in expandedSections) {
                        item { EmptySectionRow("Bientôt disponible") }
                    }

                    item {
                        WorkspaceSectionHeader(
                            title = "Modèles",
                            expanded = "templates" in expandedSections,
                            onToggle = { toggle("templates") },
                        )
                    }
                    if ("templates" in expandedSections) {
                        item { EmptySectionRow("Bientôt disponible") }
                    }

                    item {
                        WorkspaceSectionHeader(
                            title = "Corbeille",
                            expanded = "trash" in expandedSections,
                            onToggle = { toggle("trash") },
                        )
                    }
                    if ("trash" in expandedSections) {
                        item { EmptySectionRow("Bientôt disponible") }
                    }

                    item {
                        Box(modifier = Modifier.padding(bottom = SuperAppTheme.spacing.space64))
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(SuperAppTheme.spacing.space16),
        )
    }
}
