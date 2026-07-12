package com.adrien.superapp.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.common.formatRelativeTime
import com.adrien.superapp.core.designsystem.component.SuperAvatar
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.component.SuperIconButton
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.Post
import com.adrien.superapp.core.model.PostWithAuthor
import com.adrien.superapp.core.model.Profile

@Composable
fun PostCard(
    postWithAuthor: PostWithAuthor,
    onClick: () -> Unit,
    onToggleReaction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (post, author, reactionCount, replyCount, viewerHasReacted) = postWithAuthor

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                    vertical = SuperAppTheme.spacing.space16,
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SuperAvatar(name = author.displayName, size = 36.dp)
                Column(modifier = Modifier.padding(start = SuperAppTheme.spacing.space12)) {
                    Text(
                        text = author.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = SuperAppTheme.extendedColors.textPrimary,
                    )
                    Text(
                        text = "@${author.handle} · ${formatRelativeTime(post.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuperAppTheme.extendedColors.textSecondary,
                    )
                }
            }

            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyLarge,
                color = SuperAppTheme.extendedColors.textPrimary,
                modifier = Modifier.padding(top = SuperAppTheme.spacing.space8),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SuperAppTheme.spacing.space8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SuperIconButton(
                    icon = if (viewerHasReacted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (viewerHasReacted) "Retirer la réaction" else "Réagir",
                    onClick = onToggleReaction,
                )
                Text(
                    text = reactionCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SuperAppTheme.extendedColors.textSecondary,
                )
                Spacer(modifier = Modifier.width(SuperAppTheme.spacing.space16))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = null,
                    tint = SuperAppTheme.extendedColors.textSecondary,
                )
                Text(
                    text = replyCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SuperAppTheme.extendedColors.textSecondary,
                    modifier = Modifier.padding(start = SuperAppTheme.spacing.space4),
                )
            }
        }

        SuperDivider()
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun PostCardPreview() {
    SuperAppTheme {
        PostCard(
            postWithAuthor = PostWithAuthor(
                post = Post(
                    id = "1",
                    authorId = "a1",
                    text = "Bienvenue sur Super App — tout fonctionne hors connexion.",
                    createdAt = System.currentTimeMillis() - 3_600_000,
                    updatedAt = System.currentTimeMillis(),
                ),
                author = Profile(
                    id = "a1",
                    handle = "alex.demo",
                    displayName = "Alex Demo",
                    createdAt = 0,
                    updatedAt = 0,
                ),
                reactionCount = 3,
                replyCount = 1,
                viewerHasReacted = false,
            ),
            onClick = {},
            onToggleReaction = {},
        )
    }
}
