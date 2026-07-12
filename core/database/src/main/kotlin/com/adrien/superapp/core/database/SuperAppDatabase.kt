package com.adrien.superapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adrien.superapp.core.database.dao.PostDao
import com.adrien.superapp.core.database.dao.ProfileDao
import com.adrien.superapp.core.database.dao.ReactionDao
import com.adrien.superapp.core.database.entity.PostEntity
import com.adrien.superapp.core.database.entity.ProfileEntity
import com.adrien.superapp.core.database.entity.ReactionEntity

@Database(
    entities = [ProfileEntity::class, PostEntity::class, ReactionEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class SuperAppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun postDao(): PostDao
    abstract fun reactionDao(): ReactionDao
}
