package com.adrien.superapp.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.designsystem.theme.SuperAppCoverColors
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun PageCoverPickerContent(
    hasCover: Boolean,
    onColorSelected: (String) -> Unit,
    onCoverRemoved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(bottom = SuperAppTheme.spacing.space24)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.padding(horizontal = SuperAppTheme.spacing.screenHorizontal),
        ) {
            items(SuperAppCoverColors.entries.toList()) { (key, color) ->
                Box(
                    modifier = Modifier
                        .padding(SuperAppTheme.spacing.space8)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable(onClickLabel = key) { onColorSelected(key) },
                )
            }
        }
        if (hasCover) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClickLabel = "Supprimer la couverture", onClick = onCoverRemoved)
                    .padding(
                        horizontal = SuperAppTheme.spacing.screenHorizontal,
                        vertical = SuperAppTheme.spacing.space16,
                    ),
            ) {
                Text(
                    text = "Supprimer la couverture",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SuperAppTheme.extendedColors.error,
                )
            }
        }
    }
}
