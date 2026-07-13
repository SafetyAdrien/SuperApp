package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.common.formatRelativeTime
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyType

/**
 * Shared by the Table and List views — `showProperties` is the only difference between them
 * (List shows just the title, Table also shows a horizontally scrollable row of visible
 * property values), so one composable covers both rather than duplicating the title/click/
 * divider boilerplate in two files.
 */
@Composable
fun CollectionEntryRow(
    entry: CollectionEntry,
    properties: List<CollectionProperty>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showProperties: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                    vertical = SuperAppTheme.spacing.space12,
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (entry.page.icon != null) {
                    Text(text = entry.page.icon, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.width(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Article,
                        contentDescription = null,
                        tint = SuperAppTheme.extendedColors.textSecondary,
                        modifier = Modifier.width(24.dp),
                    )
                }
                Text(
                    text = entry.page.title.ifBlank { "Sans titre" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = SuperAppTheme.extendedColors.textPrimary,
                    modifier = Modifier.padding(start = SuperAppTheme.spacing.space12),
                )
            }

            val visibleProperties = properties.filter { it.visible }
            if (showProperties && visibleProperties.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = SuperAppTheme.spacing.space8, start = 36.dp),
                    horizontalArrangement = Arrangement.spacedBy(SuperAppTheme.spacing.space16),
                ) {
                    visibleProperties.forEach { property ->
                        val value = displayValue(entry, property)
                        if (!value.isNullOrBlank()) {
                            Text(
                                text = "${property.name}: $value",
                                style = MaterialTheme.typography.labelMedium,
                                color = SuperAppTheme.extendedColors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
        SuperDivider()
    }
}

/**
 * `SELECT`/`MULTI_SELECT`/`STATUS`/`PERSON` values are stored as human-readable text directly
 * (an option's label, or a profile's display name) rather than an id needing a lookup — see
 * docs/DECISIONS.md. `CREATED_BY` still holds a raw profile id with no resolver wired into this
 * compact row (a real multi-user "who" picker is future work); it's simply not shown here.
 */
private fun displayValue(entry: CollectionEntry, property: CollectionProperty): String? = when (property.type) {
    CollectionPropertyType.CREATED_AT -> formatRelativeTime(entry.page.createdAt)
    CollectionPropertyType.UPDATED_AT -> formatRelativeTime(entry.page.updatedAt)
    CollectionPropertyType.CREATED_BY -> null
    CollectionPropertyType.CHECKBOX -> if (entry.values[property.id] == "true") "✓" else null
    else -> entry.values[property.id]
}
