package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.PageRepository
import javax.inject.Inject

class UpdatePageCoverUseCase @Inject constructor(
    private val pageRepository: PageRepository,
) {
    suspend operator fun invoke(pageId: String, coverColorKey: String?): AppResult<Unit> =
        pageRepository.updateCoverColor(pageId, coverColorKey)
}
