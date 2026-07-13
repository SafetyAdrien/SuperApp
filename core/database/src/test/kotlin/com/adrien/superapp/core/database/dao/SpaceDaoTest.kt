package com.adrien.superapp.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.adrien.superapp.core.database.SuperAppDatabase
import com.adrien.superapp.core.database.entity.PageEntity
import com.adrien.superapp.core.database.entity.ProfileEntity
import com.adrien.superapp.core.database.entity.SpaceEntity
import com.adrien.superapp.core.database.entity.SpaceMemberEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SpaceDaoTest {

    private lateinit var database: SuperAppDatabase
    private lateinit var profileDao: ProfileDao
    private lateinit var spaceDao: SpaceDao
    private lateinit var pageDao: PageDao

    private val owner = ProfileEntity(
        id = "profile-1",
        handle = "alex.demo",
        displayName = "Alex Demo",
        biography = null,
        avatarUrl = null,
        bannerUrl = null,
        createdAt = 0,
        updatedAt = 0,
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SuperAppDatabase::class.java,
        ).allowMainThreadQueries().build()
        profileDao = database.profileDao()
        spaceDao = database.spaceDao()
        pageDao = database.pageDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndCountSpaces() = runTest {
        profileDao.insert(owner)
        spaceDao.insert(space(id = "space-1"))

        assertThat(spaceDao.count()).isEqualTo(1)
    }

    @Test
    fun observeSpacesForMemberComputesMemberAndPageCounts() = runTest {
        profileDao.insert(owner)
        spaceDao.insert(space(id = "space-1"))
        spaceDao.insertMember(
            SpaceMemberEntity(spaceId = "space-1", profileId = owner.id, role = "OWNER", joinedAt = 0),
        )
        pageDao.insert(page(id = "page-1", spaceId = "space-1"))
        pageDao.insert(page(id = "page-2", spaceId = "space-1"))

        val rows = spaceDao.observeSpacesForMember(owner.id).first()

        assertThat(rows).hasSize(1)
        assertThat(rows.single().space.id).isEqualTo("space-1")
        assertThat(rows.single().memberCount).isEqualTo(1)
        assertThat(rows.single().pageCount).isEqualTo(2)
    }

    @Test
    fun observeSpacesForMemberExcludesSpacesTheProfileDidNotJoin() = runTest {
        profileDao.insert(owner)
        spaceDao.insert(space(id = "space-1"))
        // No membership row inserted for "space-1" — the viewer never joined it.

        val rows = spaceDao.observeSpacesForMember(owner.id).first()

        assertThat(rows).isEmpty()
    }

    private fun space(id: String) = SpaceEntity(
        id = id,
        name = "Space $id",
        description = null,
        icon = null,
        ownerId = owner.id,
        visibility = "PRIVATE",
        createdAt = 0,
        updatedAt = 0,
    )

    private fun page(id: String, spaceId: String) = PageEntity(
        id = id,
        spaceId = spaceId,
        parentPageId = null,
        title = "Page $id",
        icon = null,
        coverUrl = null,
        coverColorKey = null,
        collectionId = null,
        createdBy = owner.id,
        createdAt = 0,
        updatedAt = 0,
        archivedAt = null,
    )
}
