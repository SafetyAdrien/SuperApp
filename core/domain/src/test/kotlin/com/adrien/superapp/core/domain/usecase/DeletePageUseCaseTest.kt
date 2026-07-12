package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Page
import com.adrien.superapp.core.testing.repository.FakePageRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeletePageUseCaseTest {

    private val pageRepository = FakePageRepository()
    private lateinit var deletePage: DeletePageUseCase

    @Before
    fun setUp() {
        deletePage = DeletePageUseCase(pageRepository)
    }

    @Test
    fun `deleting a page removes it from its space's page list`() = runTest {
        pageRepository.addPage(page(id = "page-1"))
        pageRepository.addPage(page(id = "page-2"))

        val result = deletePage("page-1")

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val remaining = pageRepository.observePages("space-1").first()
        assertThat(remaining.map { it.id }).containsExactly("page-2")
    }

    private fun page(id: String) = Page(
        id = id,
        spaceId = "space-1",
        title = "Page $id",
        createdBy = "profile-1",
        createdAt = 0,
        updatedAt = 0,
    )
}
