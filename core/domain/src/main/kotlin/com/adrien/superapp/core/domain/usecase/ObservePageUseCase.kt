package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.PageRepository
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePageUseCase @Inject constructor(
    private val pageRepository: PageRepository,
) {
    operator fun invoke(pageId: String): Flow<Page?> = pageRepository.observePage(pageId)
}
