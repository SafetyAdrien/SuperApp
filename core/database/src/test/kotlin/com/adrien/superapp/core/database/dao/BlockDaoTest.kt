package com.adrien.superapp.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.adrien.superapp.core.database.SuperAppDatabase
import com.adrien.superapp.core.database.entity.BlockEntity
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
class BlockDaoTest {

    private lateinit var database: SuperAppDatabase
    private lateinit var profileDao: ProfileDao
    private lateinit var spaceDao: SpaceDao
    private lateinit var pageDao: PageDao
    private lateinit var blockDao: BlockDao

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
    private val page = PageEntity(
        id = "page-1",
        spaceId = space.id,
        parentPageId = null,
        title = "Page",
        icon = null,
        coverUrl = null,
        coverColorKey = null,
        createdBy = owner.id,
        createdAt = 0,
        updatedAt = 0,
        archivedAt = null,
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
        blockDao = database.blockDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeBlocksForPageOrdersByPosition() = runTest {
        seedPage()
        blockDao.insert(block(id = "block-2", position = 2000))
        blockDao.insert(block(id = "block-1", position = 1000))

        val blocks = blockDao.observeBlocksForPage(page.id).first()

        assertThat(blocks.map { it.id }).containsExactly("block-1", "block-2").inOrder()
    }

    @Test
    fun deleteRemovesOnlyThatBlock() = runTest {
        seedPage()
        blockDao.insert(block(id = "block-1", position = 1000))
        blockDao.insert(block(id = "block-2", position = 2000))

        blockDao.delete("block-1")

        assertThat(blockDao.count()).isEqualTo(1)
        assertThat(blockDao.get("block-2")).isNotNull()
    }

    @Test
    fun updateChangesContentAndChecked() = runTest {
        seedPage()
        val original = block(id = "block-1", position = 1000)
        blockDao.insert(original)

        blockDao.update(original.copy(content = "Updated", checked = true))

        val updated = blockDao.get("block-1")
        assertThat(updated?.content).isEqualTo("Updated")
        assertThat(updated?.checked).isTrue()
    }

    private suspend fun seedPage() {
        profileDao.insert(owner)
        spaceDao.insert(space)
        pageDao.insert(page)
    }

    private fun block(id: String, position: Long) = BlockEntity(
        id = id,
        pageId = page.id,
        parentBlockId = null,
        type = "PARAGRAPH",
        position = position,
        content = "Content $id",
        checked = false,
        language = null,
        createdAt = 0,
        updatedAt = 0,
    )
}
