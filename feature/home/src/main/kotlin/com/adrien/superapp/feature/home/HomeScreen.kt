package com.adrien.superapp.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperFloatingActionButton
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import kotlinx.coroutines.launch

private val TabTitles = listOf("Pour vous", "Abonnements", "Activité")

@Composable
fun HomeScreen(
    onPostClick: (String) -> Unit,
    onComposeClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SuperTopAppBar(
                title = "Super App",
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch { snackbarHostState.showSnackbar("Recherche — bientôt disponible") }
                        },
                    ) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Rechercher")
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch { snackbarHostState.showSnackbar("Notifications — bientôt disponible") }
                        },
                    ) {
                        Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                },
            )
            TabRow(selectedTabIndex = selectedTab) {
                TabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title) },
                    )
                }
            }

            when (selectedTab) {
                0 -> ForYouFeed(viewModel = viewModel, onPostClick = onPostClick)
                1 -> SuperEmptyState(
                    title = "Aucun abonnement",
                    subtitle = "Les publications des personnes et espaces que vous suivez apparaîtront ici.",
                    modifier = Modifier.fillMaxSize(),
                )
                else -> SuperEmptyState(
                    title = "Aucune activité",
                    subtitle = "Les réponses, réactions et mentions de vos espaces apparaîtront ici.",
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        SuperFloatingActionButton(
            contentDescription = "Nouvelle publication",
            onClick = onComposeClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(SuperAppTheme.spacing.space16),
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(SuperAppTheme.spacing.space16),
        )
    }
}

@Composable
private fun ForYouFeed(
    viewModel: HomeViewModel,
    onPostClick: (String) -> Unit,
) {
    val posts = viewModel.feed.collectAsLazyPagingItems()

    if (posts.itemCount == 0 && posts.loadState.refresh is LoadState.Loading) {
        SuperLoadingIndicator()
        return
    }

    if (posts.itemCount == 0 && posts.loadState.refresh !is LoadState.Loading) {
        SuperEmptyState(
            title = "Votre fil est vide",
            subtitle = "Les publications de vos espaces et abonnements apparaîtront ici.",
            icon = Icons.AutoMirrored.Filled.Article,
            modifier = Modifier.fillMaxSize(),
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = SuperAppTheme.spacing.space64),
    ) {
        items(count = posts.itemCount, key = posts.itemKey { it.post.id }) { index ->
            val postWithAuthor = posts[index] ?: return@items
            PostCard(
                postWithAuthor = postWithAuthor,
                onClick = { onPostClick(postWithAuthor.post.id) },
                onToggleReaction = { viewModel.onToggleReaction(postWithAuthor.post.id) },
            )
        }
    }
}
