package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperAvatar
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.SpaceWithStats

@Composable
fun SpaceListItem(
    spaceWithStats: SpaceWithStats,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                    vertical = SuperAppTheme.spacing.space12,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SuperAvatar(name = spaceWithStats.space.name)
            Column(modifier = Modifier.padding(start = SuperAppTheme.spacing.space12)) {
                Text(
                    text = spaceWithStats.space.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = SuperAppTheme.extendedColors.textPrimary,
                )
                Text(
                    text = "${spaceWithStats.memberCount} membre(s) · ${spaceWithStats.pageCount} page(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = SuperAppTheme.extendedColors.textSecondary,
                )
            }
        }
        SuperDivider()
    }
}
