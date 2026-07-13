package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.component.SuperIconButton
import com.adrien.superapp.core.designsystem.component.SuperPrimaryButton
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.theme.SuperAppCoverColors
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.hasOptions

private val ColorKeys = SuperAppCoverColors.keys.toList()

@Composable
fun CollectionPropertiesSheetContent(
    properties: List<CollectionProperty>,
    optionsByProperty: Map<String, List<CollectionPropertyOption>>,
    onRename: (String, String) -> Unit,
    onToggleVisible: (String, Boolean) -> Unit,
    onMove: (String, MoveDirection) -> Unit,
    onDelete: (String) -> Unit,
    onAddOption: (String, String, String) -> Unit,
    onDeleteOption: (String) -> Unit,
    onCreateProperty: (String, CollectionPropertyType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedPropertyId by remember { mutableStateOf<String?>(null) }
    var showAddForm by remember { mutableStateOf(false) }

    LazyColumn(modifier = modifier.padding(bottom = SuperAppTheme.spacing.space16)) {
        item {
            Text(
                text = "Propriétés",
                style = MaterialTheme.typography.titleMedium,
                color = SuperAppTheme.extendedColors.textPrimary,
                modifier = Modifier.padding(
                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                    vertical = SuperAppTheme.spacing.space12,
                ),
            )
        }
        items(items = properties, key = { it.id }) { property ->
            PropertyRow(
                property = property,
                expanded = expandedPropertyId == property.id,
                options = optionsByProperty[property.id].orEmpty(),
                onToggleExpanded = {
                    expandedPropertyId = if (expandedPropertyId == property.id) null else property.id
                },
                onRename = { name -> onRename(property.id, name) },
                onToggleVisible = { onToggleVisible(property.id, !property.visible) },
                onMoveUp = { onMove(property.id, MoveDirection.UP) },
                onMoveDown = { onMove(property.id, MoveDirection.DOWN) },
                onDelete = { onDelete(property.id) },
                onAddOption = { label ->
                    val colorKey = ColorKeys[optionsByProperty[property.id].orEmpty().size % ColorKeys.size]
                    onAddOption(property.id, label, colorKey)
                },
                onDeleteOption = onDeleteOption,
            )
        }
        item {
            if (showAddForm) {
                AddPropertyForm(
                    onCreate = { name, type ->
                        onCreateProperty(name, type)
                        showAddForm = false
                    },
                    onCancel = { showAddForm = false },
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClickLabel = "Ajouter une propriété") { showAddForm = true }
                        .padding(
                            horizontal = SuperAppTheme.spacing.screenHorizontal,
                            vertical = SuperAppTheme.spacing.space16,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = SuperAppTheme.extendedColors.textSecondary)
                    Text(
                        text = "Ajouter une propriété",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SuperAppTheme.extendedColors.textSecondary,
                        modifier = Modifier.padding(start = SuperAppTheme.spacing.space8),
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyRow(
    property: CollectionProperty,
    expanded: Boolean,
    options: List<CollectionPropertyOption>,
    onToggleExpanded: () -> Unit,
    onRename: (String) -> Unit,
    onToggleVisible: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    onAddOption: (String) -> Unit,
    onDeleteOption: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SuperAppTheme.spacing.screenHorizontal, vertical = SuperAppTheme.spacing.space8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SuperTextField(
                value = property.name,
                onValueChange = onRename,
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            SuperIconButton(
                icon = if (property.visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = if (property.visible) "Masquer" else "Afficher",
                onClick = onToggleVisible,
            )
            SuperIconButton(icon = Icons.Filled.KeyboardArrowUp, contentDescription = "Monter", onClick = onMoveUp)
            SuperIconButton(icon = Icons.Filled.KeyboardArrowDown, contentDescription = "Descendre", onClick = onMoveDown)
            SuperIconButton(icon = Icons.Filled.Delete, contentDescription = "Supprimer", onClick = onDelete)
        }
        if (property.type.hasOptions) {
            Text(
                text = if (expanded) "Masquer les options" else "Options (${options.size})",
                style = MaterialTheme.typography.labelMedium,
                color = SuperAppTheme.extendedColors.info,
                modifier = Modifier
                    .clickable(onClick = onToggleExpanded)
                    .padding(horizontal = SuperAppTheme.spacing.screenHorizontal, vertical = SuperAppTheme.spacing.space4),
            )
            if (expanded) {
                OptionsEditor(
                    options = options,
                    onAddOption = onAddOption,
                    onDeleteOption = onDeleteOption,
                )
            }
        }
        SuperDivider()
    }
}

@Composable
private fun OptionsEditor(
    options: List<CollectionPropertyOption>,
    onAddOption: (String) -> Unit,
    onDeleteOption: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var newLabel by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(
            start = SuperAppTheme.spacing.space32,
            end = SuperAppTheme.spacing.screenHorizontal,
            bottom = SuperAppTheme.spacing.space8,
        ),
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = SuperAppTheme.spacing.space4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuperAppTheme.extendedColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                SuperIconButton(icon = Icons.Filled.Delete, contentDescription = "Supprimer l'option", onClick = { onDeleteOption(option.id) })
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            SuperTextField(
                value = newLabel,
                onValueChange = { newLabel = it },
                placeholder = "Nouvelle option",
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            SuperIconButton(
                icon = Icons.Filled.Add,
                contentDescription = "Ajouter l'option",
                onClick = {
                    if (newLabel.isNotBlank()) {
                        onAddOption(newLabel.trim())
                        newLabel = ""
                    }
                },
            )
        }
    }
}

@Composable
private fun AddPropertyForm(
    onCreate: (String, CollectionPropertyType) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(CollectionPropertyType.TEXT) }
    var showTypeMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(SuperAppTheme.spacing.screenHorizontal)) {
        SuperTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nom de la propriété",
            singleLine = true,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SuperAppTheme.spacing.space8)
                .clickable(onClickLabel = "Type de propriété") { showTypeMenu = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Type : ${propertyTypeLabel(type)}", style = MaterialTheme.typography.bodyLarge)
            DropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                CollectionPropertyType.entries.filterNot { it.name.startsWith("CREATED") || it.name == "UPDATED_AT" }.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(propertyTypeLabel(option)) },
                        onClick = { type = option; showTypeMenu = false },
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = SuperAppTheme.spacing.space16),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(SuperAppTheme.spacing.space8),
        ) {
            SuperPrimaryButton(
                text = "Ajouter",
                enabled = name.isNotBlank(),
                onClick = { onCreate(name.trim(), type) },
            )
        }
    }
}

private fun propertyTypeLabel(type: CollectionPropertyType): String = when (type) {
    CollectionPropertyType.TEXT -> "Texte"
    CollectionPropertyType.NUMBER -> "Nombre"
    CollectionPropertyType.SELECT -> "Sélection"
    CollectionPropertyType.MULTI_SELECT -> "Sélection multiple"
    CollectionPropertyType.STATUS -> "Statut"
    CollectionPropertyType.DATE -> "Date"
    CollectionPropertyType.CHECKBOX -> "Case à cocher"
    CollectionPropertyType.URL -> "URL"
    CollectionPropertyType.EMAIL -> "E-mail"
    CollectionPropertyType.PHONE -> "Téléphone"
    CollectionPropertyType.PERSON -> "Personne"
    CollectionPropertyType.CREATED_AT -> "Date de création"
    CollectionPropertyType.UPDATED_AT -> "Date de modification"
    CollectionPropertyType.CREATED_BY -> "Auteur"
}
