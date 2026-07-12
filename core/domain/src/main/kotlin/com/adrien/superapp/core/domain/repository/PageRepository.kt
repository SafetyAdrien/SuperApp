package com.adrien.superapp.core.domain.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.Flow

interface PageRepository {
    /** Top-level pages of a space — nested sub-pages land with the block editor phase. */
    fun observePages(spaceId: String): Flow<List<Page>>

    fun observePage(pageId: String): Flow<Page?>

    suspend fun createPage(spaceId: String, title: String, createdBy: String): AppResult<Page>

    suspend fun renamePage(pageId: String, title: String): AppResult<Unit>
}
