package com.adrien.superapp.feature.spaces

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.collection.CollectionViewEngine
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.domain.usecase.AddOptionUseCase
import com.adrien.superapp.core.domain.usecase.CreatePageUseCase
import com.adrien.superapp.core.domain.usecase.CreatePropertyUseCase
import com.adrien.superapp.core.domain.usecase.CreateViewUseCase
import com.adrien.superapp.core.domain.usecase.DeleteOptionUseCase
import com.adrien.superapp.core.domain.usecase.DeletePropertyUseCase
import com.adrien.superapp.core.domain.usecase.DeleteViewUseCase
import com.adrien.superapp.core.domain.usecase.MovePropertyUseCase
import com.adrien.superapp.core.domain.usecase.ObserveCollectionUseCase
import com.adrien.superapp.core.domain.usecase.ObserveEntriesUseCase
import com.adrien.superapp.core.domain.usecase.ObserveOptionsUseCase
import com.adrien.superapp.core.domain.usecase.ObservePropertiesUseCase
import com.adrien.superapp.core.domain.usecase.ObserveViewsUseCase
import com.adrien.superapp.core.domain.usecase.RenamePropertyUseCase
import com.adrien.superapp.core.domain.usecase.SetPropertyVisibleUseCase
import com.adrien.superapp.core.domain.usecase.UpdateViewUseCase
import com.adrien.superapp.core.model.Collection
import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import com.adrien.superapp.core.model.hasOptions
import com.adrien.superapp.core.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Base state before `optionsByProperty` is merged in — kept separate because the option flows
 * are dynamic (one per select-like property) and need their own `flatMapLatest`/`combine(Iterable)`
 * pipeline, which would push the main combine past the 5-flow typed-lambda limit if inlined.
 */
private data class BaseState(
    val collection: Collection?,
    val properties: List<CollectionProperty>,
    val views: List<CollectionView>,
    val activeViewId: String?,
    val entries: List<CollectionEntry>,
    val isCreatingEntry: Boolean,
    val createdEntryPageId: String?,
)

private data class Extras(
    val selectedViewId: String?,
    val isCreatingEntry: Boolean,
    val createdEntryPageId: String?,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCollectionUseCase: ObserveCollectionUseCase,
    observePropertiesUseCase: ObservePropertiesUseCase,
    observeViewsUseCase: ObserveViewsUseCase,
    observeEntriesUseCase: ObserveEntriesUseCase,
    observeOptionsUseCase: ObserveOptionsUseCase,
    private val createPageUseCase: CreatePageUseCase,
    private val createPropertyUseCase: CreatePropertyUseCase,
    private val renamePropertyUseCase: RenamePropertyUseCase,
    private val setPropertyVisibleUseCase: SetPropertyVisibleUseCase,
    private val movePropertyUseCase: MovePropertyUseCase,
    private val deletePropertyUseCase: DeletePropertyUseCase,
    private val addOptionUseCase: AddOptionUseCase,
    private val deleteOptionUseCase: DeleteOptionUseCase,
    private val createViewUseCase: CreateViewUseCase,
    private val updateViewUseCase: UpdateViewUseCase,
    private val deleteViewUseCase: DeleteViewUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.CollectionDetail>()

    private val selectedViewId = MutableStateFlow<String?>(null)
    private val isCreatingEntry = MutableStateFlow(false)
    private val createdEntryPageId = MutableStateFlow<String?>(null)

    private val extras = combine(selectedViewId, isCreatingEntry, createdEntryPageId) { viewId, creating, createdId ->
        Extras(viewId, creating, createdId)
    }

    private val baseState: Flow<BaseState> = combine(
        observeCollectionUseCase(route.collectionId),
        observePropertiesUseCase(route.collectionId),
        observeViewsUseCase(route.collectionId),
        observeEntriesUseCase(route.collectionId),
        extras,
    ) { collection, properties, views, entries, extraState ->
        val activeView = views.firstOrNull { it.id == extraState.selectedViewId } ?: views.firstOrNull()
        val displayEntries = activeView?.let { CollectionViewEngine.apply(entries, it) } ?: entries
        BaseState(
            collection = collection,
            properties = properties,
            views = views,
            activeViewId = activeView?.id,
            entries = displayEntries,
            isCreatingEntry = extraState.isCreatingEntry,
            createdEntryPageId = extraState.createdEntryPageId,
        )
    }

    private val optionsByProperty = observePropertiesUseCase(route.collectionId)
        .flatMapLatest { properties ->
            val optionProperties = properties.filter { it.type.hasOptions }
            if (optionProperties.isEmpty()) {
                flowOf(emptyMap<String, List<CollectionPropertyOption>>())
            } else {
                combine(
                    optionProperties.map { property ->
                        observeOptionsUseCase(property.id).map { options -> property.id to options }
                    },
                ) { pairs -> pairs.toMap() }
            }
        }

    val uiState: StateFlow<CollectionDetailUiState> = combine(baseState, optionsByProperty) { base, options ->
        CollectionDetailUiState(
            collection = base.collection,
            properties = base.properties,
            optionsByProperty = options,
            views = base.views,
            activeViewId = base.activeViewId,
            entries = base.entries,
            isLoading = false,
            isCreatingEntry = base.isCreatingEntry,
            createdEntryPageId = base.createdEntryPageId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CollectionDetailUiState(),
    )

    fun onSelectView(viewId: String) {
        selectedViewId.value = viewId
    }

    fun onCreateEntry() {
        if (isCreatingEntry.value) return
        val spaceId = uiState.value.collection?.spaceId ?: return
        viewModelScope.launch {
            isCreatingEntry.value = true
            when (val result = createPageUseCase(spaceId = spaceId, collectionId = route.collectionId)) {
                is AppResult.Success -> createdEntryPageId.value = result.data.id
                is AppResult.Failure -> Unit
            }
            isCreatingEntry.value = false
        }
    }

    fun onCreateProperty(name: String, type: CollectionPropertyType) {
        viewModelScope.launch { createPropertyUseCase(route.collectionId, name, type) }
    }

    fun onRenameProperty(propertyId: String, name: String) {
        viewModelScope.launch { renamePropertyUseCase(propertyId, name) }
    }

    fun onTogglePropertyVisible(propertyId: String, visible: Boolean) {
        viewModelScope.launch { setPropertyVisibleUseCase(propertyId, visible) }
    }

    fun onMoveProperty(propertyId: String, direction: MoveDirection) {
        viewModelScope.launch { movePropertyUseCase(propertyId, direction) }
    }

    fun onDeleteProperty(propertyId: String) {
        viewModelScope.launch { deletePropertyUseCase(propertyId) }
    }

    fun onAddOption(propertyId: String, label: String, colorKey: String) {
        viewModelScope.launch { addOptionUseCase(propertyId, label, colorKey) }
    }

    fun onDeleteOption(optionId: String) {
        viewModelScope.launch { deleteOptionUseCase(optionId) }
    }

    fun onCreateView(name: String, type: CollectionViewType) {
        viewModelScope.launch {
            when (val result = createViewUseCase(route.collectionId, name, type)) {
                is AppResult.Success -> selectedViewId.value = result.data.id
                is AppResult.Failure -> Unit
            }
        }
    }

    fun onUpdateView(view: CollectionView) {
        viewModelScope.launch { updateViewUseCase(view) }
    }

    fun onDeleteView(viewId: String) {
        viewModelScope.launch {
            deleteViewUseCase(viewId)
            if (selectedViewId.value == viewId) selectedViewId.value = null
        }
    }
}
