package com.adrien.superapp.feature.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

private val PageIconOptions = listOf("📄", "📝", "💡", "📌", "✅", "📚", "🎯", "🗂️", "📆", "🚀", "🌱", "⭐")

@Composable
fun PageIconPickerContent(
    hasIcon: Boolean,
    onIconSelected: (String) -> Unit,
    onIconRemoved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(bottom = SuperAppTheme.spacing.space24)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.padding(horizontal = SuperAppTheme.spacing.screenHorizontal),
        ) {
            items(PageIconOptions) { icon ->
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(SuperAppTheme.spacing.space8)
                        .clickable(onClickLabel = icon) { onIconSelected(icon) },
                )
            }
        }
        if (hasIcon) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClickLabel = "Supprimer l'icône", onClick = onIconRemoved)
                    .padding(
                        horizontal = SuperAppTheme.spacing.screenHorizontal,
                        vertical = SuperAppTheme.spacing.space16,
                    ),
            ) {
                Text(
                    text = "Supprimer l'icône",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SuperAppTheme.extendedColors.error,
                )
            }
        }
    }
}
