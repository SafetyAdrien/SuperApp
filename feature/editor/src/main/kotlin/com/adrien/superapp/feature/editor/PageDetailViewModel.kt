package com.adrien.superapp.feature.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.domain.usecase.ChangeBlockTypeUseCase
import com.adrien.superapp.core.domain.usecase.CreateBlockUseCase
import com.adrien.superapp.core.domain.usecase.DeleteBlockUseCase
import com.adrien.superapp.core.domain.usecase.DeletePageUseCase
import com.adrien.superapp.core.domain.usecase.MoveBlockUseCase
import com.adrien.superapp.core.domain.usecase.ObserveBlocksUseCase
import com.adrien.superapp.core.domain.usecase.ObserveCurrentProfileUseCase
import com.adrien.superapp.core.domain.usecase.ObserveEntryValuesUseCase
import com.adrien.superapp.core.domain.usecase.ObserveOptionsUseCase
import com.adrien.superapp.core.domain.usecase.ObservePageUseCase
import com.adrien.superapp.core.domain.usecase.ObservePropertiesUseCase
import com.adrien.superapp.core.domain.usecase.ObserveSpaceUseCase
import com.adrien.superapp.core.domain.usecase.RenamePageUseCase
import com.adrien.superapp.core.domain.usecase.SetPropertyValueUseCase
import com.adrien.superapp.core.domain.usecase.ToggleBlockCheckedUseCase
import com.adrien.superapp.core.domain.usecase.UpdateBlockContentUseCase
import com.adrien.superapp.core.domain.usecase.UpdatePageCoverUseCase
import com.adrien.superapp.core.domain.usecase.UpdatePageIconUseCase
import com.adrien.superapp.core.model.BlockType
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.hasOptions
import com.adrien.superapp.core.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val FreeTextPropertyTypes = setOf(
    CollectionPropertyType.TEXT,
    CollectionPropertyType.NUMBER,
    CollectionPropertyType.URL,
    CollectionPropertyType.EMAIL,
    CollectionPropertyType.PHONE,
    CollectionPropertyType.DATE,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observePageUseCase: ObservePageUseCase,
    observeBlocksUseCase: ObserveBlocksUseCase,
    observePropertiesUseCase: ObservePropertiesUseCase,
    observeOptionsUseCase: ObserveOptionsUseCase,
    observeEntryValuesUseCase: ObserveEntryValuesUseCase,
    observeCurrentProfileUseCase: ObserveCurrentProfileUseCase,
    private val observeSpaceUseCase: ObserveSpaceUseCase,
    private val renamePageUseCase: RenamePageUseCase,
    private val updatePageIconUseCase: UpdatePageIconUseCase,
    private val updatePageCoverUseCase: UpdatePageCoverUseCase,
    private val deletePageUseCase: DeletePageUseCase,
    private val createBlockUseCase: CreateBlockUseCase,
    private val updateBlockContentUseCase: UpdateBlockContentUseCase,
    private val changeBlockTypeUseCase: ChangeBlockTypeUseCase,
    private val toggleBlockCheckedUseCase: ToggleBlockCheckedUseCase,
    private val deleteBlockUseCase: DeleteBlockUseCase,
    private val moveBlockUseCase: MoveBlockUseCase,
    private val setPropertyValueUseCase: SetPropertyValueUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.PageDetail>()

    private val _uiState = MutableStateFlow(PageDetailUiState())
    val uiState: StateFlow<PageDetailUiState> = _uiState.asStateFlow()

    /**
     * Only the first load seeds [PageDetailUiState.title] — later emissions are our own writes
     * echoing back. `icon`/`coverColorKey` don't need this guard: they only ever change via a
     * single atomic bottom-sheet pick, never character-by-character, so always mirroring the DB
     * value is both safe and desirable.
     */
    private var titleInitialized = false
    private var spaceNameFetched = false

    /**
     * Adopts the DB's block list membership/order/type/checked (all set by single atomic
     * actions, safe to sync) but keeps each existing block's locally-typed [Block.content] —
     * otherwise a slightly-stale DB echo of an earlier keystroke could revert a fast typer's
     * later keystrokes mid-edit.
     */
    private var blocksInitialized = false

    /** Same DB-echo-vs-local-typing concern as [blocksInitialized], scoped to free-text property values. */
    private var propertyValuesInitialized = false

    init {
        viewModelScope.launch {
            observePageUseCase(route.pageId).collect { page ->
                if (page == null) {
                    _uiState.update { it.copy(isLoading = false, notFound = true) }
                    return@collect
                }

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        title = if (titleInitialized) state.title else page.title,
                        icon = page.icon,
                        coverColorKey = page.coverColorKey,
                        collectionId = page.collectionId,
                        createdAt = page.createdAt,
                        updatedAt = page.updatedAt,
                        createdBy = page.createdBy,
                    )
                }
                titleInitialized = true

                if (!spaceNameFetched) {
                    spaceNameFetched = true
                    val space = observeSpaceUseCase(page.spaceId).firstOrNull()
                    _uiState.update { it.copy(spaceName = space?.space?.name) }
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
        // Only pages that are collection entries have a schema — properties/options track the
        // page's own `collectionId` (via `observePageUseCase`) rather than the immutable route.
        viewModelScope.launch {
            observePageUseCase(route.pageId)
                .map { it?.collectionId }
                .distinctUntilChanged()
                .flatMapLatest { collectionId ->
                    if (collectionId == null) {
                        flowOf(emptyList<CollectionProperty>() to emptyMap<String, List<CollectionPropertyOption>>())
                    } else {
                        observePropertiesUseCase(collectionId).flatMapLatest { properties ->
                            val optionProperties = properties.filter { it.type.hasOptions }
                            if (optionProperties.isEmpty()) {
                                flowOf(properties to emptyMap<String, List<CollectionPropertyOption>>())
                            } else {
                                combine(
                                    optionProperties.map { property ->
                                        observeOptionsUseCase(property.id).map { options -> property.id to options }
                                    },
                                ) { pairs -> properties to pairs.toMap() }
                            }
                        }
                    }
                }
                .collect { (properties, options) ->
                    _uiState.update { it.copy(properties = properties, optionsByProperty = options) }
                }
        }
        viewModelScope.launch {
            observeEntryValuesUseCase(route.pageId).collect { dbValues ->
                _uiState.update { state ->
                    val merged = if (!propertyValuesInitialized) {
                        propertyValuesInitialized = true
                        dbValues
                    } else {
                        // Free-text property types are typed character by character, same race as
                        // block content; select/checkbox/person types are set atomically so always
                        // adopting the DB value for them is safe (and picks up remote deletions).
                        val freeTextPropertyIds = state.properties
                            .filter { it.type in FreeTextPropertyTypes }
                            .map { it.id }
                            .toSet()
                        dbValues + state.propertyValues.filterKeys { it in freeTextPropertyIds }
                    }
                    state.copy(propertyValues = merged)
                }
            }
        }
        viewModelScope.launch {
            observeCurrentProfileUseCase().collect { profile ->
                _uiState.update { it.copy(currentProfileId = profile?.id) }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
        viewModelScope.launch { renamePageUseCase(route.pageId, title) }
    }

    fun onIconSelected(icon: String?) {
        viewModelScope.launch { updatePageIconUseCase(route.pageId, icon) }
    }

    fun onCoverColorSelected(coverColorKey: String?) {
        viewModelScope.launch { updatePageCoverUseCase(route.pageId, coverColorKey) }
    }

    fun onDeletePage() {
        viewModelScope.launch {
            deletePageUseCase(route.pageId)
            _uiState.update { it.copy(deleted = true) }
        }
    }

    fun onInsertBlock(afterBlockId: String?, type: BlockType) {
        viewModelScope.launch { createBlockUseCase(pageId = route.pageId, afterBlockId = afterBlockId, type = type) }
    }

    fun onChangeBlockType(blockId: String, type: BlockType) {
        viewModelScope.launch { changeBlockTypeUseCase(blockId, type) }
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

    fun onSetPropertyValue(propertyId: String, value: String?) {
        _uiState.update { state ->
            val updatedValues = if (value == null) state.propertyValues - propertyId else state.propertyValues + (propertyId to value)
            state.copy(propertyValues = updatedValues)
        }
        viewModelScope.launch { setPropertyValueUseCase(route.pageId, propertyId, value) }
    }
}
