package com.adrien.superapp.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.component.SuperIconButton
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.theme.SuperAppShapes
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.BlockType

@Composable
fun BlockRow(
    block: Block,
    numberedIndex: Int?,
    onContentChange: (String) -> Unit,
    onToggleChecked: () -> Unit,
    onInsertBelow: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space4,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (block.type) {
                BlockType.DIVIDER -> SuperDivider(modifier = Modifier.padding(top = SuperAppTheme.spacing.space20))
                BlockType.CHECKLIST -> Row(verticalAlignment = Alignment.Top) {
                    Checkbox(checked = block.checked, onCheckedChange = { onToggleChecked() })
                    BlockContentField(
                        block = block,
                        prefix = null,
                        onContentChange = onContentChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                BlockType.CALLOUT -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = SuperAppTheme.extendedColors.info.copy(alpha = 0.08f),
                            shape = SuperAppShapes.medium,
                        )
                        .padding(SuperAppTheme.spacing.space8),
                ) {
                    BlockContentField(block = block, prefix = null, onContentChange = onContentChange)
                }
                else -> {
                    val prefix = when (block.type) {
                        BlockType.BULLETED_LIST -> "•"
                        BlockType.NUMBERED_LIST -> "${numberedIndex ?: 1}."
                        else -> null
                    }
                    BlockContentField(block = block, prefix = prefix, onContentChange = onContentChange)
                }
            }
        }

        BlockActionsMenu(
            onInsertBelow = onInsertBelow,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onDelete = onDelete,
        )
    }
}

@Composable
private fun BlockContentField(
    block: Block,
    prefix: String?,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseStyle = when (block.type) {
        BlockType.HEADING_1 -> MaterialTheme.typography.headlineMedium
        BlockType.HEADING_2 -> MaterialTheme.typography.titleLarge
        BlockType.HEADING_3 -> MaterialTheme.typography.titleMedium
        BlockType.CODE -> MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
        BlockType.QUOTE -> MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
        else -> MaterialTheme.typography.bodyLarge
    }
    val style = if (block.type == BlockType.CHECKLIST && block.checked) {
        baseStyle.copy(textDecoration = TextDecoration.LineThrough)
    } else {
        baseStyle
    }
    val singleLine = block.type in setOf(
        BlockType.HEADING_1, BlockType.HEADING_2, BlockType.HEADING_3,
        BlockType.BULLETED_LIST, BlockType.NUMBERED_LIST, BlockType.CHECKLIST,
    )

    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        if (prefix != null) {
            Text(
                text = prefix,
                style = style,
                color = SuperAppTheme.extendedColors.textSecondary,
                modifier = Modifier.padding(top = SuperAppTheme.spacing.space16, end = SuperAppTheme.spacing.space8),
            )
        }
        SuperTextField(
            value = block.content,
            onValueChange = onContentChange,
            singleLine = singleLine,
            textStyle = style,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun BlockActionsMenu(
    onInsertBelow: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        SuperIconButton(
            icon = Icons.Filled.MoreVert,
            contentDescription = "Actions du bloc",
            onClick = { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Insérer en dessous") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                onClick = { expanded = false; onInsertBelow() },
            )
            DropdownMenuItem(
                text = { Text("Monter") },
                leadingIcon = { Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = null) },
                onClick = { expanded = false; onMoveUp() },
            )
            DropdownMenuItem(
                text = { Text("Descendre") },
                leadingIcon = { Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = null) },
                onClick = { expanded = false; onMoveDown() },
            )
            DropdownMenuItem(
                text = { Text("Supprimer") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Delete, contentDescription = null) },
                onClick = { expanded = false; onDelete() },
            )
        }
    }
}
