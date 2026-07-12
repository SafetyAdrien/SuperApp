package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.model.Page
import com.adrien.superapp.core.testing.repository.FakePageRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObserveRecentPagesUseCaseTest {

    private val pageRepository = FakePageRepository()
    private lateinit var observeRecentPages: ObserveRecentPagesUseCase

    @Before
    fun setUp() {
        observeRecentPages = ObserveRecentPagesUseCase(pageRepository)
    }

    @Test
    fun `returns pages most recently updated first, capped at the limit`() = runTest {
        pageRepository.addPage(page(id = "old", updatedAt = 1_000))
        pageRepository.addPage(page(id = "newest", updatedAt = 3_000))
        pageRepository.addPage(page(id = "middle", updatedAt = 2_000))

        val recent = observeRecentPages(limit = 2).first()

        assertThat(recent.map { it.id }).containsExactly("newest", "middle").inOrder()
    }

    private fun page(id: String, updatedAt: Long) = Page(
        id = id,
        spaceId = "space-1",
        title = "Page $id",
        createdBy = "profile-1",
        createdAt = updatedAt,
        updatedAt = updatedAt,
    )
}
