package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.testing.repository.FakeCollectionRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MovePropertyUseCaseTest {

    private val collectionRepository = FakeCollectionRepository()
    private lateinit var createProperty: CreatePropertyUseCase
    private lateinit var moveProperty: MovePropertyUseCase

    @Before
    fun setUp() {
        createProperty = CreatePropertyUseCase(collectionRepository)
        moveProperty = MovePropertyUseCase(collectionRepository)
    }

    @Test
    fun `moving the second property up swaps it with the first`() = runTest {
        val first = (createProperty("collection-1", "Status", CollectionPropertyType.TEXT) as AppResult.Success).data
        val second = (createProperty("collection-1", "Priority", CollectionPropertyType.TEXT) as AppResult.Success).data

        moveProperty(second.id, MoveDirection.UP)

        val ordered = collectionRepository.observeProperties("collection-1").first()
        assertThat(ordered.map { it.id }).containsExactly(second.id, first.id).inOrder()
    }

    @Test
    fun `moving the first property up is a no-op`() = runTest {
        val first = (createProperty("collection-1", "Status", CollectionPropertyType.TEXT) as AppResult.Success).data
        val second = (createProperty("collection-1", "Priority", CollectionPropertyType.TEXT) as AppResult.Success).data

        val result = moveProperty(first.id, MoveDirection.UP)

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val ordered = collectionRepository.observeProperties("collection-1").first()
        assertThat(ordered.map { it.id }).containsExactly(first.id, second.id).inOrder()
    }

    @Test
    fun `moving the last property down is a no-op`() = runTest {
        val first = (createProperty("collection-1", "Status", CollectionPropertyType.TEXT) as AppResult.Success).data
        val second = (createProperty("collection-1", "Priority", CollectionPropertyType.TEXT) as AppResult.Success).data

        moveProperty(second.id, MoveDirection.DOWN)

        val ordered = collectionRepository.observeProperties("collection-1").first()
        assertThat(ordered.map { it.id }).containsExactly(first.id, second.id).inOrder()
    }
}
