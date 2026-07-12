package com.adrien.superapp.core.database.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.database.dao.ProfileDao
import com.adrien.superapp.core.database.mapper.toEntity
import com.adrien.superapp.core.database.mapper.toModel
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
) : ProfileRepository {

    override fun observeCurrentProfile(): Flow<Profile?> =
        profileDao.observeCurrentProfile().map { it?.toModel() }

    override fun observeProfile(profileId: String): Flow<Profile?> =
        profileDao.observeProfile(profileId).map { it?.toModel() }

    override suspend fun updateProfile(profile: Profile): AppResult<Unit> = try {
        profileDao.update(profile.toEntity())
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }
}
