package com.adrien.superapp.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.adrien.superapp.core.database.SuperAppDatabase
import com.adrien.superapp.core.database.entity.PostEntity
import com.adrien.superapp.core.database.entity.ProfileEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PostDaoTest {

    private lateinit var database: SuperAppDatabase
    private lateinit var profileDao: ProfileDao
    private lateinit var postDao: PostDao

    private val author = ProfileEntity(
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
        postDao = database.postDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndCountPosts() = runTest {
        profileDao.insert(author)
        postDao.insert(post(id = "post-1"))

        assertThat(postDao.count()).isEqualTo(1)
    }

    @Test
    fun observePostJoinsAuthorAndComputesZeroCounts() = runTest {
        profileDao.insert(author)
        postDao.insert(post(id = "post-1"))

        val row = postDao.observePost(postId = "post-1", viewerId = author.id).first()

        assertThat(row).isNotNull()
        assertThat(row!!.author.handle).isEqualTo(author.handle)
        assertThat(row.reactionCount).isEqualTo(0)
        assertThat(row.replyCount).isEqualTo(0)
        assertThat(row.viewerHasReacted).isFalse()
    }

    @Test
    fun observeRepliesReturnsOnlyMatchingReplyToPostId() = runTest {
        profileDao.insert(author)
        postDao.insert(post(id = "post-1"))
        postDao.insert(post(id = "reply-1", replyToPostId = "post-1"))
        postDao.insert(post(id = "unrelated"))

        val replies = postDao.observeReplies(postId = "post-1", viewerId = author.id).first()

        assertThat(replies).hasSize(1)
        assertThat(replies.single().post.id).isEqualTo("reply-1")
    }

    private fun post(id: String, replyToPostId: String? = null) = PostEntity(
        id = id,
        authorId = author.id,
        spaceId = null,
        text = "Post $id",
        replyToPostId = replyToPostId,
        quotedPostId = null,
        visibility = "PUBLIC",
        createdAt = 0,
        updatedAt = 0,
    )
}
