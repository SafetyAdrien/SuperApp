package com.adrien.superapp.core.database.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.database.dao.PageDao
import com.adrien.superapp.core.database.mapper.toEntity
import com.adrien.superapp.core.database.mapper.toModel
import com.adrien.superapp.core.domain.repository.PageRepository
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PageRepositoryImpl @Inject constructor(
    private val pageDao: PageDao,
    private val profileRepository: ProfileRepository,
) : PageRepository {

    override fun observePages(spaceId: String): Flow<List<Page>> =
        pageDao.observePagesForSpace(spaceId).map { rows -> rows.map { it.toModel() } }

    override fun observeRecentPages(limit: Int): Flow<List<Page>> =
        profileRepository.observeCurrentProfile().filterNotNull().flatMapLatest { viewer ->
            pageDao.observeRecentPagesForMember(viewer.id, limit).map { rows -> rows.map { it.toModel() } }
        }

    override fun observePage(pageId: String): Flow<Page?> =
        pageDao.observePage(pageId).map { it?.toModel() }

    override suspend fun createPage(
        spaceId: String,
        title: String,
        createdBy: String,
        collectionId: String?,
    ): AppResult<Page> = try {
        val now = System.currentTimeMillis()
        val page = Page(
            id = UUID.randomUUID().toString(),
            spaceId = spaceId,
            title = title,
            collectionId = collectionId,
            createdBy = createdBy,
            createdAt = now,
            updatedAt = now,
        )
        pageDao.insert(page.toEntity())
        AppResult.Success(page)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun renamePage(pageId: String, title: String): AppResult<Unit> = try {
        val existing = pageDao.observePage(pageId).filterNotNull().first()
        pageDao.update(existing.copy(title = title, updatedAt = System.currentTimeMillis()))
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun updateIcon(pageId: String, icon: String?): AppResult<Unit> = try {
        val existing = pageDao.observePage(pageId).filterNotNull().first()
        pageDao.update(existing.copy(icon = icon, updatedAt = System.currentTimeMillis()))
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun updateCoverColor(pageId: String, coverColorKey: String?): AppResult<Unit> = try {
        val existing = pageDao.observePage(pageId).filterNotNull().first()
        pageDao.update(existing.copy(coverColorKey = coverColorKey, updatedAt = System.currentTimeMillis()))
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun deletePage(pageId: String): AppResult<Unit> = try {
        pageDao.delete(pageId)
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }
}
