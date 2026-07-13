package com.adrien.superapp.feature.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.common.formatRelativeTime
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType

/**
 * One property editor on a collection-entry page, dispatched by [CollectionProperty.type]. System
 * types ([CollectionPropertyType.CREATED_AT]/`UPDATED_AT`/`CREATED_BY`) read straight off the
 * owning page's own fields and are read-only — they're never stored as a value row (see
 * `isSystemProperty`). [currentProfileId] powers the `PERSON` "assign to me" toggle; `CREATED_BY`
 * still shows the raw profile id with no name resolver wired in (same documented gap as
 * `CollectionEntryRow`).
 */
@Composable
fun PropertyValueRow(
    property: CollectionProperty,
    pageCreatedAt: Long,
    pageUpdatedAt: Long,
    pageCreatedBy: String,
    value: String?,
    options: List<CollectionPropertyOption>,
    currentProfileId: String?,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SuperAppTheme.spacing.screenHorizontal, vertical = SuperAppTheme.spacing.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = property.name,
            style = MaterialTheme.typography.labelLarge,
            color = SuperAppTheme.extendedColors.textSecondary,
            modifier = Modifier.padding(end = SuperAppTheme.spacing.space12),
        )

        when (property.type) {
            CollectionPropertyType.CREATED_AT -> ReadOnlyValue(formatRelativeTime(pageCreatedAt))
            CollectionPropertyType.UPDATED_AT -> ReadOnlyValue(formatRelativeTime(pageUpdatedAt))
            CollectionPropertyType.CREATED_BY -> ReadOnlyValue(pageCreatedBy)

            CollectionPropertyType.CHECKBOX -> Checkbox(
                checked = value == "true",
                onCheckedChange = { checked -> onValueChange(if (checked) "true" else null) },
            )

            CollectionPropertyType.SELECT, CollectionPropertyType.STATUS -> SingleSelectPicker(
                value = value,
                options = options,
                onValueChange = onValueChange,
            )

            CollectionPropertyType.MULTI_SELECT -> MultiSelectPicker(
                value = value,
                options = options,
                onValueChange = onValueChange,
            )

            CollectionPropertyType.PERSON -> PersonToggle(
                value = value,
                currentProfileId = currentProfileId,
                onValueChange = onValueChange,
            )

            CollectionPropertyType.DATE -> SuperTextField(
                value = value.orEmpty(),
                onValueChange = { onValueChange(it.ifBlank { null }) },
                placeholder = "AAAA-MM-JJ",
                singleLine = true,
                modifier = Modifier.weight(1f),
            )

            CollectionPropertyType.TEXT,
            CollectionPropertyType.NUMBER,
            CollectionPropertyType.URL,
            CollectionPropertyType.EMAIL,
            CollectionPropertyType.PHONE,
            -> SuperTextField(
                value = value.orEmpty(),
                onValueChange = { onValueChange(it.ifBlank { null }) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ReadOnlyValue(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = SuperAppTheme.extendedColors.textPrimary, modifier = modifier)
}

@Composable
private fun SingleSelectPicker(
    value: String?,
    options: List<CollectionPropertyOption>,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = modifier.clickable(onClickLabel = value ?: "Choisir") { expanded = true }) {
        Text(
            text = value ?: "Aucune",
            style = MaterialTheme.typography.bodyMedium,
            color = SuperAppTheme.extendedColors.textPrimary,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Aucune") }, onClick = { expanded = false; onValueChange(null) })
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option.label) }, onClick = { expanded = false; onValueChange(option.label) })
            }
        }
    }
}

@Composable
private fun MultiSelectPicker(
    value: String?,
    options: List<CollectionPropertyOption>,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabels = value.orEmpty().split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    Row(modifier = modifier.clickable(onClickLabel = "Sélectionner") { expanded = true }) {
        Text(
            text = if (selectedLabels.isEmpty()) "Aucune" else selectedLabels.joinToString(", "),
            style = MaterialTheme.typography.bodyMedium,
            color = SuperAppTheme.extendedColors.textPrimary,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                val checked = option.label in selectedLabels
                DropdownMenuItem(
                    text = { Text(if (checked) "✓ ${option.label}" else option.label) },
                    onClick = {
                        val newSelection = if (checked) selectedLabels - option.label else selectedLabels + option.label
                        onValueChange(newSelection.takeIf { it.isNotEmpty() }?.joinToString(","))
                    },
                )
            }
        }
    }
}

@Composable
private fun PersonToggle(
    value: String?,
    currentProfileId: String?,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = when {
        value == null -> "Non assigné"
        value == currentProfileId -> "Vous"
        else -> value
    }
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = SuperAppTheme.extendedColors.info,
        modifier = modifier.clickable(onClickLabel = "Basculer l'assignation") {
            onValueChange(if (value == currentProfileId) null else currentProfileId)
        },
    )
}
