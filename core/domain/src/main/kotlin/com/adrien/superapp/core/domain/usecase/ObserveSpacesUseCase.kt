package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.SpaceRepository
import com.adrien.superapp.core.model.SpaceWithStats
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSpacesUseCase @Inject constructor(
    private val spaceRepository: SpaceRepository,
) {
    operator fun invoke(): Flow<List<SpaceWithStats>> = spaceRepository.observeSpaces()
}
