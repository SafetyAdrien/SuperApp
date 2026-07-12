package com.adrien.superapp.core.database.di

import android.content.Context
import androidx.room.Room
import com.adrien.superapp.core.database.SuperAppDatabase
import com.adrien.superapp.core.database.dao.PostDao
import com.adrien.superapp.core.database.dao.ProfileDao
import com.adrien.superapp.core.database.dao.ReactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "superapp.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesSuperAppDatabase(@ApplicationContext context: Context): SuperAppDatabase =
        Room.databaseBuilder(context, SuperAppDatabase::class.java, DATABASE_NAME).build()

    @Provides
    fun providesProfileDao(database: SuperAppDatabase): ProfileDao = database.profileDao()

    @Provides
    fun providesPostDao(database: SuperAppDatabase): PostDao = database.postDao()

    @Provides
    fun providesReactionDao(database: SuperAppDatabase): ReactionDao = database.reactionDao()
}
