package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Profile
import com.adrien.superapp.core.testing.repository.FakePostRepository
import com.adrien.superapp.core.testing.repository.FakeProfileRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreatePostUseCaseTest {

    private val postRepository = FakePostRepository()
    private val profileRepository = FakeProfileRepository()
    private lateinit var createPost: CreatePostUseCase

    private val demoProfile = Profile(
        id = "profile-1",
        handle = "alex.demo",
        displayName = "Alex Demo",
        createdAt = 0,
        updatedAt = 0,
    )

    @Before
    fun setUp() {
        createPost = CreatePostUseCase(postRepository, profileRepository)
        profileRepository.setCurrentProfile(demoProfile)
    }

    @Test
    fun `creates a post with trimmed text as the current profile`() = runTest {
        val result = createPost(text = "  Hello Super App  ")

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val post = (result as AppResult.Success).data
        assertThat(post.text).isEqualTo("Hello Super App")
        assertThat(post.authorId).isEqualTo(demoProfile.id)
        assertThat(post.replyToPostId).isNull()
    }

    @Test
    fun `blank text fails without hitting the repository`() = runTest {
        val result = createPost(text = "   ")

        assertThat(result).isInstanceOf(AppResult.Failure::class.java)
        assertThat((result as AppResult.Failure).error).isInstanceOf(AppError.Unknown::class.java)
    }

    @Test
    fun `fails with NotFound when there is no current profile`() = runTest {
        val noProfileRepository = FakeProfileRepository()
        val useCase = CreatePostUseCase(postRepository, noProfileRepository)

        val result = useCase(text = "Hello")

        assertThat(result).isInstanceOf(AppResult.Failure::class.java)
        assertThat((result as AppResult.Failure).error).isEqualTo(AppError.NotFound)
    }
}
