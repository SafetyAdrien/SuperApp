package com.adrien.superapp.feature.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.domain.usecase.CreateBlockUseCase
import com.adrien.superapp.core.domain.usecase.DeleteBlockUseCase
import com.adrien.superapp.core.domain.usecase.MoveBlockUseCase
import com.adrien.superapp.core.domain.usecase.ObserveBlocksUseCase
import com.adrien.superapp.core.domain.usecase.ObservePageUseCase
import com.adrien.superapp.core.domain.usecase.RenamePageUseCase
import com.adrien.superapp.core.domain.usecase.ToggleBlockCheckedUseCase
import com.adrien.superapp.core.domain.usecase.UpdateBlockContentUseCase
import com.adrien.superapp.core.model.BlockType
import com.adrien.superapp.core.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observePageUseCase: ObservePageUseCase,
    observeBlocksUseCase: ObserveBlocksUseCase,
    private val renamePageUseCase: RenamePageUseCase,
    private val createBlockUseCase: CreateBlockUseCase,
    private val updateBlockContentUseCase: UpdateBlockContentUseCase,
    private val toggleBlockCheckedUseCase: ToggleBlockCheckedUseCase,
    private val deleteBlockUseCase: DeleteBlockUseCase,
    private val moveBlockUseCase: MoveBlockUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.PageDetail>()

    private val _uiState = MutableStateFlow(PageDetailUiState())
    val uiState: StateFlow<PageDetailUiState> = _uiState.asStateFlow()

    /** Only the first load seeds [PageDetailUiState.title] — later emissions are our own writes echoing back. */
    private var titleInitialized = false

    /**
     * Adopts the DB's block list membership/order/type/checked (all set by single atomic
     * actions, safe to sync) but keeps each existing block's locally-typed [Block.content] —
     * otherwise a slightly-stale DB echo of an earlier keystroke could revert a fast typer's
     * later keystrokes mid-edit.
     */
    private var blocksInitialized = false

    init {
        viewModelScope.launch {
            observePageUseCase(route.pageId).collect { page ->
                when {
                    page == null -> _uiState.update { it.copy(isLoading = false, notFound = true) }
                    !titleInitialized -> {
                        titleInitialized = true
                        _uiState.update { it.copy(isLoading = false, title = page.title) }
                    }
                    else -> Unit
                }
            }
        }
        viewModelScope.launch {
            observeBlocksUseCase(route.pageId).collect { dbBlocks ->
                _uiState.update { state ->
                    val merged = if (!blocksInitialized) {
                        blocksInitialized = true
                        dbBlocks
                    } else {
                        val localById = state.blocks.associateBy { it.id }
                        dbBlocks.map { dbBlock -> localById[dbBlock.id]?.let { dbBlock.copy(content = it.content) } ?: dbBlock }
                    }
                    state.copy(blocks = merged)
                }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
        viewModelScope.launch { renamePageUseCase(route.pageId, title) }
    }

    fun onInsertBlock(afterBlockId: String?, type: BlockType) {
        viewModelScope.launch { createBlockUseCase(pageId = route.pageId, afterBlockId = afterBlockId, type = type) }
    }

    fun onBlockContentChanged(blockId: String, content: String) {
        _uiState.update { state ->
            state.copy(blocks = state.blocks.map { if (it.id == blockId) it.copy(content = content) else it })
        }
        viewModelScope.launch { updateBlockContentUseCase(blockId, content) }
    }

    fun onToggleBlockChecked(blockId: String) {
        viewModelScope.launch { toggleBlockCheckedUseCase(blockId) }
    }

    fun onDeleteBlock(blockId: String) {
        viewModelScope.launch { deleteBlockUseCase(blockId) }
    }

    fun onMoveBlock(blockId: String, direction: MoveDirection) {
        viewModelScope.launch { moveBlockUseCase(blockId, direction) }
    }
}
