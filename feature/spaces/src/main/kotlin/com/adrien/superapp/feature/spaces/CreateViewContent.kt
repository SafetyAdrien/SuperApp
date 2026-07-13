package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.adrien.superapp.core.designsystem.component.SuperPrimaryButton
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.CollectionViewType

@Composable
fun CreateViewContent(
    onCreate: (String, CollectionViewType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(CollectionViewType.TABLE) }
    var showTypeMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(SuperAppTheme.spacing.screenHorizontal)) {
        Text(
            text = "Nouvelle vue",
            style = MaterialTheme.typography.titleMedium,
            color = SuperAppTheme.extendedColors.textPrimary,
            modifier = Modifier.padding(bottom = SuperAppTheme.spacing.space12),
        )
        SuperTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nom de la vue",
            singleLine = true,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SuperAppTheme.spacing.space8)
                .clickable(onClickLabel = "Type de vue") { showTypeMenu = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Type : ${viewTypeLabel(type)}", style = MaterialTheme.typography.bodyLarge)
            DropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                CollectionViewType.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(viewTypeLabel(option)) },
                        onClick = { type = option; showTypeMenu = false },
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = SuperAppTheme.spacing.space16),
            horizontalArrangement = Arrangement.spacedBy(SuperAppTheme.spacing.space8),
        ) {
            SuperPrimaryButton(
                text = "Créer",
                enabled = name.isNotBlank(),
                onClick = { onCreate(name.trim(), type) },
            )
        }
    }
}

fun viewTypeLabel(type: CollectionViewType): String = when (type) {
    CollectionViewType.TABLE -> "Tableau"
    CollectionViewType.LIST -> "Liste"
    CollectionViewType.KANBAN -> "Kanban"
}
