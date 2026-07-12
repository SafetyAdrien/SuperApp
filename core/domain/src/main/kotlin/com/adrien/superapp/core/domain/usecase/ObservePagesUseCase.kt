package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.PageRepository
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePagesUseCase @Inject constructor(
    private val pageRepository: PageRepository,
) {
    operator fun invoke(spaceId: String): Flow<List<Page>> = pageRepository.observePages(spaceId)
}
