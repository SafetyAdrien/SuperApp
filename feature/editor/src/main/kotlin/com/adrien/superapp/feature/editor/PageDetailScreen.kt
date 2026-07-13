package com.adrien.superapp.feature.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adrien.superapp.core.designsystem.component.SuperBottomSheet
import com.adrien.superapp.core.designsystem.component.SuperDivider
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

private enum class PagePickerTarget { ICON, COVER }

@Composable
fun PageDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PageDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Slash-command insert vs. append-at-end both reuse the same type-picker sheet;
    // slashBlockId distinguishes "transform this block" (slash) from "insert a new one".
    var pickerAfterBlockId by remember { mutableStateOf<String?>(null) }
    var slashBlockId by remember { mutableStateOf<String?>(null) }
    var showTypePicker by rememberSaveable { mutableStateOf(false) }
    var pagePickerTarget by remember { mutableStateOf<PagePickerTarget?>(null) }
    var showPageMenu by remember { mutableStateOf(false) }
    var selectedBlockId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) onBackClick()
    }

    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(
            title = uiState.spaceName ?: "Page",
            onBackClick = onBackClick,
            actions = {
                IconButton(onClick = { showPageMenu = true }) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Options de la page")
                }
                DropdownMenu(expanded = showPageMenu, onDismissRequest = { showPageMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Changer l'icône") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.InsertEmoticon, contentDescription = null) },
                        onClick = {
                            showPageMenu = false
                            pagePickerTarget = PagePickerTarget.ICON
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Changer la couverture") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Image, contentDescription = null) },
                        onClick = {
                            showPageMenu = false
                            pagePickerTarget = PagePickerTarget.COVER
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Supprimer la page") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Delete, contentDescription = null) },
                        onClick = {
                            showPageMenu = false
                            viewModel.onDeletePage()
                        },
                    )
                }
            },
        )

        when {
            uiState.isLoading -> SuperLoadingIndicator()
            uiState.notFound -> SuperEmptyState(
                title = "Page introuvable",
                subtitle = "Elle a peut-être été supprimée.",
                modifier = Modifier.fillMaxSize(),
            )
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    PageCoverBand(
                        coverColorKey = uiState.coverColorKey,
                        onClick = { pagePickerTarget = PagePickerTarget.COVER },
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SuperAppTheme.spacing.screenHorizontal),
                        verticalAlignment = Alignment.Top,
                    ) {
                        PageIconBadge(
                            icon = uiState.icon,
                            onClick = { pagePickerTarget = PagePickerTarget.ICON },
                            modifier = Modifier.offset(y = (-20).dp),
                        )
                    }
                }
                item {
                    SuperTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChanged,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(
                            horizontal = SuperAppTheme.spacing.screenHorizontal,
                            vertical = SuperAppTheme.spacing.space8,
                        ),
                    )
                }

                if (uiState.collectionId != null) {
                    items(items = uiState.properties, key = { "property-${it.id}" }) { property ->
                        PropertyValueRow(
                            property = property,
                            pageCreatedAt = uiState.createdAt,
                            pageUpdatedAt = uiState.updatedAt,
                            pageCreatedBy = uiState.createdBy,
                            value = uiState.propertyValues[property.id],
                            options = uiState.optionsByProperty[property.id].orEmpty(),
                            currentProfileId = uiState.currentProfileId,
                            onValueChange = { value -> viewModel.onSetPropertyValue(property.id, value) },
                        )
                    }
                    item { SuperDivider() }
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
                            selected = block.id == selectedBlockId,
                            onContentChange = { content ->
                                if (content.endsWith("/") && !block.content.endsWith("/")) {
                                    slashBlockId = block.id
                                    showTypePicker = true
                                    viewModel.onBlockContentChanged(block.id, content.dropLast(1))
                                } else {
                                    viewModel.onBlockContentChanged(block.id, content)
                                }
                            },
                            onToggleChecked = { viewModel.onToggleBlockChecked(block.id) },
                            onInsertBelow = {
                                pickerAfterBlockId = block.id
                                slashBlockId = null
                                showTypePicker = true
                            },
                            onMoveUp = { viewModel.onMoveBlock(block.id, MoveDirection.UP) },
                            onMoveDown = { viewModel.onMoveBlock(block.id, MoveDirection.DOWN) },
                            onDelete = { viewModel.onDeleteBlock(block.id) },
                            onLongPress = {
                                selectedBlockId = if (selectedBlockId == block.id) null else block.id
                            },
                        )
                    }
                }

                item {
                    AddBlockRow(
                        onClick = {
                            pickerAfterBlockId = uiState.blocks.lastOrNull()?.id
                            slashBlockId = null
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
                    val triggerId = slashBlockId
                    if (triggerId != null) {
                        viewModel.onChangeBlockType(triggerId, type)
                        slashBlockId = null
                    } else {
                        viewModel.onInsertBlock(afterBlockId = pickerAfterBlockId, type = type)
                    }
                },
            )
        }
    }

    if (pagePickerTarget != null) {
        SuperBottomSheet(onDismissRequest = { pagePickerTarget = null }) {
            when (pagePickerTarget) {
                PagePickerTarget.ICON -> PageIconPickerContent(
                    hasIcon = uiState.icon != null,
                    onIconSelected = { icon ->
                        pagePickerTarget = null
                        viewModel.onIconSelected(icon)
                    },
                    onIconRemoved = {
                        pagePickerTarget = null
                        viewModel.onIconSelected(null)
                    },
                )
                PagePickerTarget.COVER -> PageCoverPickerContent(
                    hasCover = uiState.coverColorKey != null,
                    onColorSelected = { key ->
                        pagePickerTarget = null
                        viewModel.onCoverColorSelected(key)
                    },
                    onCoverRemoved = {
                        pagePickerTarget = null
                        viewModel.onCoverColorSelected(null)
                    },
                )
                null -> Unit
            }
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
