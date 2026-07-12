package com.adrien.superapp.core.domain.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    /** The signed-in (or local demo) profile. Null until the demo seed has run. */
    fun observeCurrentProfile(): Flow<Profile?>

    fun observeProfile(profileId: String): Flow<Profile?>

    suspend fun updateProfile(profile: Profile): AppResult<Unit>
}
