package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.BlockType
import com.adrien.superapp.core.testing.repository.FakeBlockRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateBlockUseCaseTest {

    private val blockRepository = FakeBlockRepository()
    private lateinit var createBlock: CreateBlockUseCase

    @Before
    fun setUp() {
        createBlock = CreateBlockUseCase(blockRepository)
    }

    @Test
    fun `first block on an empty page gets a positive position`() = runTest {
        val result = createBlock(pageId = "page-1", type = BlockType.HEADING_1)

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val block = (result as AppResult.Success).data
        assertThat(block.pageId).isEqualTo("page-1")
        assertThat(block.type).isEqualTo(BlockType.HEADING_1)
        assertThat(block.position).isGreaterThan(0L)
    }

    @Test
    fun `inserting between two blocks lands strictly between their positions`() = runTest {
        val first = (createBlock(pageId = "page-1") as AppResult.Success).data
        val third = (createBlock(pageId = "page-1") as AppResult.Success).data

        val middle = (createBlock(pageId = "page-1", afterBlockId = first.id) as AppResult.Success).data

        assertThat(middle.position).isGreaterThan(first.position)
        assertThat(middle.position).isLessThan(third.position)
    }

    @Test
    fun `appending after the last block places it at the end`() = runTest {
        val first = (createBlock(pageId = "page-1") as AppResult.Success).data
        val second = (createBlock(pageId = "page-1", afterBlockId = first.id) as AppResult.Success).data

        assertThat(second.position).isGreaterThan(first.position)
    }
}
