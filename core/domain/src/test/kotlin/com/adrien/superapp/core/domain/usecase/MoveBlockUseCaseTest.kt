package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.testing.repository.FakeBlockRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MoveBlockUseCaseTest {

    private val blockRepository = FakeBlockRepository()
    private lateinit var createBlock: CreateBlockUseCase
    private lateinit var moveBlock: MoveBlockUseCase

    @Before
    fun setUp() {
        createBlock = CreateBlockUseCase(blockRepository)
        moveBlock = MoveBlockUseCase(blockRepository)
    }

    @Test
    fun `moving the second block up swaps it with the first`() = runTest {
        val first = (createBlock(pageId = "page-1") as AppResult.Success).data
        val second = (createBlock(pageId = "page-1") as AppResult.Success).data

        moveBlock(second.id, MoveDirection.UP)

        val ordered = blockRepository.observeBlocks("page-1").first()
        assertThat(ordered.map { it.id }).containsExactly(second.id, first.id).inOrder()
    }

    @Test
    fun `moving the first block up is a no-op`() = runTest {
        val first = (createBlock(pageId = "page-1") as AppResult.Success).data
        val second = (createBlock(pageId = "page-1") as AppResult.Success).data

        val result = moveBlock(first.id, MoveDirection.UP)

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val ordered = blockRepository.observeBlocks("page-1").first()
        assertThat(ordered.map { it.id }).containsExactly(first.id, second.id).inOrder()
    }

    @Test
    fun `moving the last block down is a no-op`() = runTest {
        val first = (createBlock(pageId = "page-1") as AppResult.Success).data
        val second = (createBlock(pageId = "page-1") as AppResult.Success).data

        moveBlock(second.id, MoveDirection.DOWN)

        val ordered = blockRepository.observeBlocks("page-1").first()
        assertThat(ordered.map { it.id }).containsExactly(first.id, second.id).inOrder()
    }
}
