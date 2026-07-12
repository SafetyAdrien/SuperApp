package com.adrien.superapp.core.testing.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.PageRepository
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FakePageRepository : PageRepository {

    private val pages = MutableStateFlow<List<Page>>(emptyList())
    private var shouldFail = false

    fun setShouldFail(fail: Boolean) {
        shouldFail = fail
    }

    fun addPage(page: Page) {
        pages.value = pages.value + page
    }

    override fun observePages(spaceId: String) = pages.map { list ->
        list.filter { it.spaceId == spaceId && it.parentPageId == null }
    }

    override fun observeRecentPages(limit: Int) = pages.map { list ->
        list.sortedByDescending { it.updatedAt }.take(limit)
    }

    override fun observePage(pageId: String) = pages.map { list -> list.firstOrNull { it.id == pageId } }

    override suspend fun createPage(spaceId: String, title: String, createdBy: String): AppResult<Page> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val now = System.currentTimeMillis()
        val page = Page(
            id = UUID.randomUUID().toString(),
            spaceId = spaceId,
            title = title,
            createdBy = createdBy,
            createdAt = now,
            updatedAt = now,
        )
        addPage(page)
        return AppResult.Success(page)
    }

    override suspend fun renamePage(pageId: String, title: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        pages.value = pages.value.map { if (it.id == pageId) it.copy(title = title) else it }
        return AppResult.Success(Unit)
    }

    override suspend fun updateIcon(pageId: String, icon: String?): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        pages.value = pages.value.map { if (it.id == pageId) it.copy(icon = icon) else it }
        return AppResult.Success(Unit)
    }

    override suspend fun updateCoverColor(pageId: String, coverColorKey: String?): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        pages.value = pages.value.map { if (it.id == pageId) it.copy(coverColorKey = coverColorKey) else it }
        return AppResult.Success(Unit)
    }

    override suspend fun deletePage(pageId: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        pages.value = pages.value.filterNot { it.id == pageId }
        return AppResult.Success(Unit)
    }
}
