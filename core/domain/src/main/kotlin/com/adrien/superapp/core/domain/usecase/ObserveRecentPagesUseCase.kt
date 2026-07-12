package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.PageRepository
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecentPagesUseCase @Inject constructor(
    private val pageRepository: PageRepository,
) {
    operator fun invoke(limit: Int = 10): Flow<List<Page>> = pageRepository.observeRecentPages(limit)
}
