package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.PageRepository
import javax.inject.Inject

class UpdatePageIconUseCase @Inject constructor(
    private val pageRepository: PageRepository,
) {
    suspend operator fun invoke(pageId: String, icon: String?): AppResult<Unit> =
        pageRepository.updateIcon(pageId, icon)
}
