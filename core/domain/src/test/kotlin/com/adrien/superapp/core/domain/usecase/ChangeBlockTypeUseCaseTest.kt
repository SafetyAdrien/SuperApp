package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.BlockType
import com.adrien.superapp.core.testing.repository.FakeBlockRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ChangeBlockTypeUseCaseTest {

    private val blockRepository = FakeBlockRepository()
    private lateinit var createBlock: CreateBlockUseCase
    private lateinit var changeBlockType: ChangeBlockTypeUseCase

    @Before
    fun setUp() {
        createBlock = CreateBlockUseCase(blockRepository)
        changeBlockType = ChangeBlockTypeUseCase(blockRepository)
    }

    @Test
    fun `changes an existing block's type without changing its position or content`() = runTest {
        val block = (createBlock(pageId = "page-1", type = BlockType.PARAGRAPH) as AppResult.Success).data
        blockRepository.updateContent(block.id, "Titre")

        val result = changeBlockType(block.id, BlockType.HEADING_1)

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val updated = blockRepository.observeBlocks("page-1").first().single()
        assertThat(updated.type).isEqualTo(BlockType.HEADING_1)
        assertThat(updated.content).isEqualTo("Titre")
        assertThat(updated.position).isEqualTo(block.position)
    }
}
