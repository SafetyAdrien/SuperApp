package com.adrien.superapp.core.database.di

import com.adrien.superapp.core.database.repository.BlockRepositoryImpl
import com.adrien.superapp.core.database.repository.CollectionRepositoryImpl
import com.adrien.superapp.core.database.repository.PageRepositoryImpl
import com.adrien.superapp.core.database.repository.PostRepositoryImpl
import com.adrien.superapp.core.database.repository.ProfileRepositoryImpl
import com.adrien.superapp.core.database.repository.ReactionRepositoryImpl
import com.adrien.superapp.core.database.repository.SpaceRepositoryImpl
import com.adrien.superapp.core.domain.repository.BlockRepository
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.domain.repository.PageRepository
import com.adrien.superapp.core.domain.repository.PostRepository
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.domain.repository.ReactionRepository
import com.adrien.superapp.core.domain.repository.SpaceRepository
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

    @Binds
    @Singleton
    abstract fun bindsSpaceRepository(impl: SpaceRepositoryImpl): SpaceRepository

    @Binds
    @Singleton
    abstract fun bindsPageRepository(impl: PageRepositoryImpl): PageRepository

    @Binds
    @Singleton
    abstract fun bindsBlockRepository(impl: BlockRepositoryImpl): BlockRepository

    @Binds
    @Singleton
    abstract fun bindsCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository
}
