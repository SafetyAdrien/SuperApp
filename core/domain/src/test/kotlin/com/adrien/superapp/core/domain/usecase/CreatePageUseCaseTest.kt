package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Profile
import com.adrien.superapp.core.testing.repository.FakePageRepository
import com.adrien.superapp.core.testing.repository.FakeProfileRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreatePageUseCaseTest {

    private val pageRepository = FakePageRepository()
    private val profileRepository = FakeProfileRepository()
    private lateinit var createPage: CreatePageUseCase

    private val demoProfile = Profile(
        id = "profile-1",
        handle = "alex.demo",
        displayName = "Alex Demo",
        createdAt = 0,
        updatedAt = 0,
    )

    @Before
    fun setUp() {
        createPage = CreatePageUseCase(pageRepository, profileRepository)
        profileRepository.setCurrentProfile(demoProfile)
    }

    @Test
    fun `creates a page authored by the current profile`() = runTest {
        val result = createPage(spaceId = "space-1", title = "Bienvenue")

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val page = (result as AppResult.Success).data
        assertThat(page.title).isEqualTo("Bienvenue")
        assertThat(page.spaceId).isEqualTo("space-1")
        assertThat(page.createdBy).isEqualTo(demoProfile.id)
    }

    @Test
    fun `blank title falls back to a default`() = runTest {
        val result = createPage(spaceId = "space-1", title = "   ")

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        assertThat((result as AppResult.Success).data.title).isEqualTo("Page sans titre")
    }

    @Test
    fun `fails with NotFound when there is no current profile`() = runTest {
        val noProfileRepository = FakeProfileRepository()
        val useCase = CreatePageUseCase(pageRepository, noProfileRepository)

        val result = useCase(spaceId = "space-1")

        assertThat(result).isInstanceOf(AppResult.Failure::class.java)
        assertThat((result as AppResult.Failure).error).isEqualTo(AppError.NotFound)
    }
}
