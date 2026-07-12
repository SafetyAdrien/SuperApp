package com.adrien.superapp.feature.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.FormatListNumbered
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.BlockType

private data class BlockTypeOption(val type: BlockType, val label: String, val icon: ImageVector)

private val BlockTypeOptions = listOf(
    BlockTypeOption(BlockType.PARAGRAPH, "Texte", Icons.Filled.Notes),
    BlockTypeOption(BlockType.HEADING_1, "Titre 1", Icons.Filled.Title),
    BlockTypeOption(BlockType.HEADING_2, "Titre 2", Icons.Filled.Title),
    BlockTypeOption(BlockType.HEADING_3, "Titre 3", Icons.Filled.Title),
    BlockTypeOption(BlockType.BULLETED_LIST, "Liste à puces", Icons.AutoMirrored.Filled.FormatListBulleted),
    BlockTypeOption(BlockType.NUMBERED_LIST, "Liste numérotée", Icons.AutoMirrored.Filled.FormatListNumbered),
    BlockTypeOption(BlockType.CHECKLIST, "Case à cocher", Icons.Filled.CheckBox),
    BlockTypeOption(BlockType.QUOTE, "Citation", Icons.Filled.FormatQuote),
    BlockTypeOption(BlockType.CALLOUT, "Encadré", Icons.Filled.Lightbulb),
    BlockTypeOption(BlockType.DIVIDER, "Séparateur", Icons.Filled.HorizontalRule),
    BlockTypeOption(BlockType.CODE, "Code", Icons.Filled.Code),
)

/** Content of the "add block" bottom sheet — reports the chosen [BlockType], nothing more. */
@Composable
fun BlockTypePickerContent(
    onTypeSelected: (BlockType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.padding(bottom = SuperAppTheme.spacing.space24)) {
        items(BlockTypeOptions) { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTypeSelected(option.type) }
                    .padding(
                        horizontal = SuperAppTheme.spacing.screenHorizontal,
                        vertical = SuperAppTheme.spacing.space16,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = SuperAppTheme.extendedColors.textSecondary,
                )
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SuperAppTheme.extendedColors.textPrimary,
                    modifier = Modifier.padding(start = SuperAppTheme.spacing.space16),
                )
            }
        }
    }
}
