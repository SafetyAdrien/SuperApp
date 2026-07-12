package com.adrien.superapp.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun PostDetailScreen(
    onBackClick: () -> Unit,
    onReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(title = "Publication", onBackClick = onBackClick)

        when {
            uiState.isLoading -> SuperLoadingIndicator()
            uiState.post == null -> SuperEmptyState(
                title = "Publication introuvable",
                subtitle = "Elle a peut-être été supprimée.",
                modifier = Modifier.fillMaxSize(),
            )
            else -> {
                val post = uiState.post!!
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item(key = post.post.id) {
                        PostCard(
                            postWithAuthor = post,
                            onClick = {},
                            onToggleReaction = { viewModel.onToggleReaction(post.post.id) },
                        )
                    }
                    if (uiState.replies.isEmpty()) {
                        item {
                            Text(
                                text = "Aucune réponse pour l'instant",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SuperAppTheme.extendedColors.textSecondary,
                                modifier = Modifier.padding(
                                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                                    vertical = SuperAppTheme.spacing.space16,
                                ),
                            )
                        }
                    } else {
                        items(items = uiState.replies, key = { it.post.id }) { reply ->
                            PostCard(
                                postWithAuthor = reply,
                                onClick = { onReplyClick(reply.post.id) },
                                onToggleReaction = { viewModel.onToggleReaction(reply.post.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
