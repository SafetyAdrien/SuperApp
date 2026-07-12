package com.adrien.superapp.core.testing.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeProfileRepository : ProfileRepository {

    private val profiles = MutableStateFlow<Map<String, Profile>>(emptyMap())
    private val currentProfileId = MutableStateFlow<String?>(null)

    fun setCurrentProfile(profile: Profile) {
        profiles.value = profiles.value + (profile.id to profile)
        currentProfileId.value = profile.id
    }

    override fun observeCurrentProfile() = currentProfileId.map { id -> id?.let { profiles.value[it] } }

    override fun observeProfile(profileId: String) = profiles.map { it[profileId] }

    override suspend fun updateProfile(profile: Profile): AppResult<Unit> {
        profiles.value = profiles.value + (profile.id to profile)
        return AppResult.Success(Unit)
    }
}
