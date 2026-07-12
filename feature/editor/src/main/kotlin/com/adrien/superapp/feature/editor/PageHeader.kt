package com.adrien.superapp.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.designsystem.theme.SuperAppCoverColors
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/** Tap to open the cover-color picker. Empty state doubles as the "add a cover" affordance. */
@Composable
fun PageCoverBand(
    coverColorKey: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = coverColorKey?.let { SuperAppCoverColors[it] }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (color != null) 96.dp else 48.dp)
            .background(color ?: MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClickLabel = "Couverture de la page", onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (color == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = null,
                    tint = SuperAppTheme.extendedColors.textSecondary,
                )
                Text(
                    text = "Ajouter une couverture",
                    style = MaterialTheme.typography.bodySmall,
                    color = SuperAppTheme.extendedColors.textSecondary,
                    modifier = Modifier.padding(start = SuperAppTheme.spacing.space8),
                )
            }
        }
    }
}

/** Tap to open the icon picker. Empty state doubles as the "add an icon" affordance. */
@Composable
fun PageIconBadge(
    icon: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClickLabel = "Icône de la page", onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Text(text = icon, style = MaterialTheme.typography.headlineMedium)
        } else {
            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = null,
                tint = SuperAppTheme.extendedColors.textSecondary,
            )
        }
    }
}
