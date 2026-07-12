package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.PageRepository
import javax.inject.Inject

class RenamePageUseCase @Inject constructor(
    private val pageRepository: PageRepository,
) {
    suspend operator fun invoke(pageId: String, title: String): AppResult<Unit> {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return AppResult.Failure(AppError.Unknown())

        return pageRepository.renamePage(pageId, trimmed)
    }
}
