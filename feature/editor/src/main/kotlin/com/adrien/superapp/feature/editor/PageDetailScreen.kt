package com.adrien.superapp.feature.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperBottomSheet
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperLoadingIndicator
import com.adrien.superapp.core.designsystem.component.SuperTextField
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.BlockType

/**
 * Precomputed once over the full list (not inside `LazyColumn`'s `items{}` lambda): lazy items
 * compose out of order and only while visible, so a running counter captured in the item-content
 * closure would produce wrong numbering as the user scrolls.
 */
private fun numberedIndices(blocks: List<Block>): List<Int?> {
    var counter = 0
    return blocks.map { block ->
        counter = if (block.type == BlockType.NUMBERED_LIST) counter + 1 else 0
        if (block.type == BlockType.NUMBERED_LIST) counter else null
    }
}

@Composable
fun PageDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PageDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var pickerAfterBlockId by remember { mutableStateOf<String?>(null) }
    var showTypePicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(title = "Page", onBackClick = onBackClick)

        when {
            uiState.isLoading -> SuperLoadingIndicator()
            uiState.notFound -> SuperEmptyState(
                title = "Page introuvable",
                subtitle = "Elle a peut-être été supprimée.",
                modifier = Modifier.fillMaxSize(),
            )
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    SuperTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChanged,
                        singleLine = true,
                        modifier = Modifier.padding(
                            horizontal = SuperAppTheme.spacing.screenHorizontal,
                            vertical = SuperAppTheme.spacing.space8,
                        ),
                    )
                }

                if (uiState.blocks.isEmpty()) {
                    item {
                        SuperEmptyState(
                            title = "Cette page est vide",
                            subtitle = "Ajoutez un premier bloc pour commencer à écrire.",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                } else {
                    val numberedIndices = numberedIndices(uiState.blocks)
                    items(items = uiState.blocks.zip(numberedIndices), key = { (block, _) -> block.id }) { (block, numberedIndex) ->
                        BlockRow(
                            block = block,
                            numberedIndex = numberedIndex,
                            onContentChange = { content -> viewModel.onBlockContentChanged(block.id, content) },
                            onToggleChecked = { viewModel.onToggleBlockChecked(block.id) },
                            onInsertBelow = {
                                pickerAfterBlockId = block.id
                                showTypePicker = true
                            },
                            onMoveUp = { viewModel.onMoveBlock(block.id, MoveDirection.UP) },
                            onMoveDown = { viewModel.onMoveBlock(block.id, MoveDirection.DOWN) },
                            onDelete = { viewModel.onDeleteBlock(block.id) },
                        )
                    }
                }

                item {
                    AddBlockRow(
                        onClick = {
                            pickerAfterBlockId = uiState.blocks.lastOrNull()?.id
                            showTypePicker = true
                        },
                    )
                }
            }
        }
    }

    if (showTypePicker) {
        SuperBottomSheet(onDismissRequest = { showTypePicker = false }) {
            BlockTypePickerContent(
                onTypeSelected = { type ->
                    showTypePicker = false
                    viewModel.onInsertBlock(afterBlockId = pickerAfterBlockId, type = type)
                },
            )
        }
    }
}

@Composable
private fun AddBlockRow(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Ajouter un bloc", onClick = onClick)
            .padding(
                horizontal = SuperAppTheme.spacing.screenHorizontal,
                vertical = SuperAppTheme.spacing.space16,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = SuperAppTheme.extendedColors.textSecondary,
        )
        Text(
            text = "Ajouter un bloc",
            style = MaterialTheme.typography.bodyLarge,
            color = SuperAppTheme.extendedColors.textSecondary,
            modifier = Modifier.padding(start = SuperAppTheme.spacing.space8),
        )
    }
}
