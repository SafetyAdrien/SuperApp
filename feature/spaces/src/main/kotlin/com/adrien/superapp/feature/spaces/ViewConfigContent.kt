package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import com.adrien.superapp.core.model.FilterOperator

/**
 * Sort/filter/(Kanban) group configuration for a single view. Every control commits immediately
 * via [onUpdate] with a modified copy of [view] — same immediate-persistence pattern used
 * elsewhere in the app (page title, block content) — so there is no separate "Save" step to forget.
 */
@Composable
fun ViewConfigContent(
    view: CollectionView,
    properties: List<CollectionProperty>,
    onUpdate: (CollectionView) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(bottom = SuperAppTheme.spacing.space16)) {
        Text(
            text = "Configurer la vue",
            style = MaterialTheme.typography.titleMedium,
            color = SuperAppTheme.extendedColors.textPrimary,
            modifier = Modifier.padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space12,
            ),
        )

        SectionLabel("Trier par")
        PropertyPicker(
            properties = properties,
            selectedId = view.sortPropertyId,
            noneLabel = "Aucun tri",
            onSelected = { id -> onUpdate(view.copy(sortPropertyId = id)) },
        )
        if (view.sortPropertyId != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SuperAppTheme.spacing.screenHorizontal, vertical = SuperAppTheme.spacing.space4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Ordre décroissant", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Switch(checked = view.sortDescending, onCheckedChange = { onUpdate(view.copy(sortDescending = it)) })
            }
        }

        SuperDivider()
        SectionLabel("Filtrer par")
        PropertyPicker(
            properties = properties,
            selectedId = view.filterPropertyId,
            noneLabel = "Aucun filtre",
            onSelected = { id ->
                onUpdate(
                    view.copy(
                        filterPropertyId = id,
                        filterOperator = if (id == null) null else FilterOperator.EQUALS,
                        filterValue = null,
                    ),
                )
            },
        )
        if (view.filterPropertyId != null) {
            FilterOperatorPicker(
                selected = view.filterOperator ?: FilterOperator.EQUALS,
                onSelected = { operator -> onUpdate(view.copy(filterOperator = operator)) },
            )
            if (view.filterOperator == FilterOperator.EQUALS || view.filterOperator == FilterOperator.CONTAINS) {
                SuperTextField(
                    value = view.filterValue.orEmpty(),
                    onValueChange = { onUpdate(view.copy(filterValue = it)) },
                    label = "Valeur",
                    singleLine = true,
                    modifier = Modifier.padding(
                        horizontal = SuperAppTheme.spacing.screenHorizontal,
                        vertical = SuperAppTheme.spacing.space4,
                    ),
                )
            }
        }

        if (view.type == CollectionViewType.KANBAN) {
            SuperDivider()
            SectionLabel("Regrouper par")
            PropertyPicker(
                properties = properties,
                selectedId = view.groupPropertyId,
                noneLabel = "Aucun regroupement",
                onSelected = { id -> onUpdate(view.copy(groupPropertyId = id)) },
            )
        }

        SuperDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = "Supprimer la vue", onClick = onDelete)
                .padding(horizontal = SuperAppTheme.spacing.screenHorizontal, vertical = SuperAppTheme.spacing.space16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = SuperAppTheme.extendedColors.error)
            Text(
                text = "Supprimer cette vue",
                style = MaterialTheme.typography.bodyLarge,
                color = SuperAppTheme.extendedColors.error,
                modifier = Modifier.padding(start = SuperAppTheme.spacing.space8),
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = SuperAppTheme.extendedColors.textSecondary,
        modifier = modifier.padding(
            horizontal = SuperAppTheme.spacing.screenHorizontal,
            vertical = SuperAppTheme.spacing.space4,
        ),
    )
}

@Composable
private fun PropertyPicker(
    properties: List<CollectionProperty>,
    selectedId: String?,
    noneLabel: String,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = properties.firstOrNull { it.id == selectedId }?.name ?: noneLabel

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = selectedName) { expanded = true }
            .padding(horizontal = SuperAppTheme.spacing.screenHorizontal, vertical = SuperAppTheme.spacing.space8),
    ) {
        Text(text = selectedName, style = MaterialTheme.typography.bodyLarge, color = SuperAppTheme.extendedColors.textPrimary)
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text(noneLabel) }, onClick = { expanded = false; onSelected(null) })
            properties.forEach { property ->
                DropdownMenuItem(text = { Text(property.name) }, onClick = { expanded = false; onSelected(property.id) })
            }
        }
    }
}

@Composable
private fun FilterOperatorPicker(
    selected: FilterOperator,
    onSelected: (FilterOperator) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = filterOperatorLabel(selected)) { expanded = true }
            .padding(horizontal = SuperAppTheme.spacing.screenHorizontal, vertical = SuperAppTheme.spacing.space8),
    ) {
        Text(
            text = filterOperatorLabel(selected),
            style = MaterialTheme.typography.bodyLarge,
            color = SuperAppTheme.extendedColors.textPrimary,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            FilterOperator.entries.forEach { operator ->
                DropdownMenuItem(text = { Text(filterOperatorLabel(operator)) }, onClick = { expanded = false; onSelected(operator) })
            }
        }
    }
}

private fun filterOperatorLabel(operator: FilterOperator): String = when (operator) {
    FilterOperator.EQUALS -> "Est égal à"
    FilterOperator.CONTAINS -> "Contient"
    FilterOperator.IS_CHECKED -> "Coché"
    FilterOperator.IS_NOT_CHECKED -> "Non coché"
    FilterOperator.IS_EMPTY -> "Vide"
    FilterOperator.IS_NOT_EMPTY -> "Non vide"
}
