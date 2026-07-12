package com.adrien.superapp.core.domain.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.Flow

interface PageRepository {
    /** Top-level pages of a space — nested sub-pages land with the block editor phase. */
    fun observePages(spaceId: String): Flow<List<Page>>

    /** Most recently updated pages across every space the current viewer is a member of. */
    fun observeRecentPages(limit: Int): Flow<List<Page>>

    fun observePage(pageId: String): Flow<Page?>

    suspend fun createPage(spaceId: String, title: String, createdBy: String): AppResult<Page>

    suspend fun renamePage(pageId: String, title: String): AppResult<Unit>

    suspend fun updateIcon(pageId: String, icon: String?): AppResult<Unit>

    suspend fun updateCoverColor(pageId: String, coverColorKey: String?): AppResult<Unit>

    suspend fun deletePage(pageId: String): AppResult<Unit>
}
