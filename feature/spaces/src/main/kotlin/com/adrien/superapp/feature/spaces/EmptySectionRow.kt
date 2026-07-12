package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/** A lightweight inline placeholder for a workspace-home section with nothing to show yet — unlike
 * [com.adrien.superapp.core.designsystem.component.SuperEmptyState], this doesn't fill the screen. */
@Composable
fun EmptySectionRow(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = SuperAppTheme.extendedColors.textTertiary,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space12,
            ),
    )
}
