package com.adrien.superapp.core.database.dao

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.adrien.superapp.core.database.entity.SpaceEntity

/** Row shape for the joined space-browser query — not a Room `@Entity`, just a query result POJO. */
data class SpaceWithStatsRow(
    @Embedded val space: SpaceEntity,
    @ColumnInfo(name = "memberCount") val memberCount: Int,
    @ColumnInfo(name = "pageCount") val pageCount: Int,
)
