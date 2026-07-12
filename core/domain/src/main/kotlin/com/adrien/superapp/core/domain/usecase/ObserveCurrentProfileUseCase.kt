package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Profile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Flow<Profile?> = profileRepository.observeCurrentProfile()
}
