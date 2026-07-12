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
class PageDaoTest {

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
    private val space = SpaceEntity(
        id = "space-1",
        name = "Space",
        description = null,
        icon = null,
        ownerId = owner.id,
        visibility = "PRIVATE",
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
    fun observeRecentPagesForMemberOrdersByUpdatedAtDescending() = runTest {
        seedSpaceWithMember()
        pageDao.insert(page(id = "page-old", updatedAt = 1_000))
        pageDao.insert(page(id = "page-newest", updatedAt = 3_000))
        pageDao.insert(page(id = "page-middle", updatedAt = 2_000))

        val recent = pageDao.observeRecentPagesForMember(owner.id, limit = 10).first()

        assertThat(recent.map { it.id }).containsExactly("page-newest", "page-middle", "page-old").inOrder()
    }

    @Test
    fun observeRecentPagesForMemberExcludesSpacesTheProfileDidNotJoin() = runTest {
        profileDao.insert(owner)
        spaceDao.insert(space)
        // No membership row inserted — the viewer never joined "space-1".
        pageDao.insert(page(id = "page-1", updatedAt = 1_000))

        val recent = pageDao.observeRecentPagesForMember(owner.id, limit = 10).first()

        assertThat(recent).isEmpty()
    }

    @Test
    fun deleteRemovesOnlyThatPage() = runTest {
        seedSpaceWithMember()
        pageDao.insert(page(id = "page-1", updatedAt = 0))
        pageDao.insert(page(id = "page-2", updatedAt = 0))

        pageDao.delete("page-1")

        assertThat(pageDao.count()).isEqualTo(1)
        assertThat(pageDao.observePage("page-2").first()).isNotNull()
    }

    private suspend fun seedSpaceWithMember() {
        profileDao.insert(owner)
        spaceDao.insert(space)
        spaceDao.insertMember(SpaceMemberEntity(spaceId = space.id, profileId = owner.id, role = "OWNER", joinedAt = 0))
    }

    private fun page(id: String, updatedAt: Long) = PageEntity(
        id = id,
        spaceId = space.id,
        parentPageId = null,
        title = "Page $id",
        icon = null,
        coverUrl = null,
        coverColorKey = null,
        createdBy = owner.id,
        createdAt = updatedAt,
        updatedAt = updatedAt,
        archivedAt = null,
    )
}
