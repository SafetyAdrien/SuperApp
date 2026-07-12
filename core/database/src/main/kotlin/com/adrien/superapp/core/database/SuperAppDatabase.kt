package com.adrien.superapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adrien.superapp.core.database.dao.BlockDao
import com.adrien.superapp.core.database.dao.PageDao
import com.adrien.superapp.core.database.dao.PostDao
import com.adrien.superapp.core.database.dao.ProfileDao
import com.adrien.superapp.core.database.dao.ReactionDao
import com.adrien.superapp.core.database.dao.SpaceDao
import com.adrien.superapp.core.database.entity.BlockEntity
import com.adrien.superapp.core.database.entity.PageEntity
import com.adrien.superapp.core.database.entity.PostEntity
import com.adrien.superapp.core.database.entity.ProfileEntity
import com.adrien.superapp.core.database.entity.ReactionEntity
import com.adrien.superapp.core.database.entity.SpaceEntity
import com.adrien.superapp.core.database.entity.SpaceMemberEntity

/**
 * Still version 1: this database has never shipped to a real device in this sandbox (see
 * PROJECT_STATUS.md §Blocages), so Phase 3 and Phase 4's new entities are added to the same
 * version rather than authoring `Migration`s no installed copy of the app will ever need.
 */
@Database(
    entities = [
        ProfileEntity::class,
        PostEntity::class,
        ReactionEntity::class,
        SpaceEntity::class,
        SpaceMemberEntity::class,
        PageEntity::class,
        BlockEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class SuperAppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun postDao(): PostDao
    abstract fun reactionDao(): ReactionDao
    abstract fun spaceDao(): SpaceDao
    abstract fun pageDao(): PageDao
    abstract fun blockDao(): BlockDao
}
