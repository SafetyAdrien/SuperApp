package com.adrien.superapp.core.database.di

import com.adrien.superapp.core.database.repository.PostRepositoryImpl
import com.adrien.superapp.core.database.repository.ProfileRepositoryImpl
import com.adrien.superapp.core.database.repository.ReactionRepositoryImpl
import com.adrien.superapp.core.domain.repository.PostRepository
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.domain.repository.ReactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    abstract fun bindsReactionRepository(impl: ReactionRepositoryImpl): ReactionRepository
}
