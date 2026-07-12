package com.adrien.superapp.feature.spaces

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/**
 * A collapsible section header for the Notion-like workspace home (Récents, Espaces, Favoris,
 * ...). [trailingAction] is an optional icon button (e.g. "+" on the Espaces section) shown
 * regardless of [expanded] — it acts independently of expand/collapse.
 */
@Composable
fun WorkspaceSectionHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    trailingAction: @Composable (() -> Unit)? = null,
) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 90f else 0f, label = "sectionChevron")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = title, role = Role.Button, onClick = onToggle)
            .padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space12,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = SuperAppTheme.extendedColors.textSecondary,
            modifier = Modifier.rotate(rotation),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = SuperAppTheme.extendedColors.textSecondary,
            modifier = Modifier
                .weight(1f)
                .padding(start = SuperAppTheme.spacing.space8),
        )
        trailingAction?.invoke()
    }
}
