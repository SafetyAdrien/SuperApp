package com.adrien.superapp.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.adrien.superapp.core.database.SuperAppDatabase
import com.adrien.superapp.core.database.entity.CollectionEntity
import com.adrien.superapp.core.database.entity.PageEntity
import com.adrien.superapp.core.database.entity.ProfileEntity
import com.adrien.superapp.core.database.entity.SpaceEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CollectionDaoTest {

    private lateinit var database: SuperAppDatabase
    private lateinit var profileDao: ProfileDao
    private lateinit var spaceDao: SpaceDao
    private lateinit var pageDao: PageDao
    private lateinit var collectionDao: CollectionDao

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
    private val collection = CollectionEntity(
        id = "collection-1",
        spaceId = space.id,
        title = "Tasks",
        icon = null,
        createdBy = owner.id,
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
        collectionDao = database.collectionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeCollectionsForSpaceReturnsInsertedCollection() = runTest {
        seedSpace()
        collectionDao.insert(collection)

        val collections = collectionDao.observeCollectionsForSpace(space.id).first()

        assertThat(collections.map { it.id }).containsExactly(collection.id)
    }

    @Test
    fun observeCollectionsForSpaceExcludesOtherSpaces() = runTest {
        seedSpace()
        collectionDao.insert(collection)

        val collections = collectionDao.observeCollectionsForSpace("space-2").first()

        assertThat(collections).isEmpty()
    }

    @Test
    fun observePagesForCollectionReturnsOnlyThatCollectionsEntries() = runTest {
        seedSpace()
        collectionDao.insert(collection)
        pageDao.insert(entryPage(id = "entry-1", collectionId = collection.id))
        pageDao.insert(entryPage(id = "entry-2", collectionId = collection.id))
        pageDao.insert(entryPage(id = "ordinary-page", collectionId = null))

        val entries = pageDao.observePagesForCollection(collection.id).first()

        assertThat(entries.map { it.id }).containsExactly("entry-1", "entry-2")
    }

    private suspend fun seedSpace() {
        profileDao.insert(owner)
        spaceDao.insert(space)
    }

    private fun entryPage(id: String, collectionId: String?) = PageEntity(
        id = id,
        spaceId = space.id,
        parentPageId = null,
        title = "Page $id",
        icon = null,
        coverUrl = null,
        coverColorKey = null,
        collectionId = collectionId,
        createdBy = owner.id,
        createdAt = 0,
        updatedAt = 0,
        archivedAt = null,
    )
}
