package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Profile
import com.adrien.superapp.core.model.SpaceVisibility
import com.adrien.superapp.core.testing.repository.FakeProfileRepository
import com.adrien.superapp.core.testing.repository.FakeSpaceRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateSpaceUseCaseTest {

    private val spaceRepository = FakeSpaceRepository()
    private val profileRepository = FakeProfileRepository()
    private lateinit var createSpace: CreateSpaceUseCase

    private val demoProfile = Profile(
        id = "profile-1",
        handle = "alex.demo",
        displayName = "Alex Demo",
        createdAt = 0,
        updatedAt = 0,
    )

    @Before
    fun setUp() {
        createSpace = CreateSpaceUseCase(spaceRepository, profileRepository)
        profileRepository.setCurrentProfile(demoProfile)
    }

    @Test
    fun `creates a space owned by the current profile with trimmed name and description`() = runTest {
        val result = createSpace(name = "  Équipe Design  ", description = "  Espace partagé  ")

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val space = (result as AppResult.Success).data
        assertThat(space.name).isEqualTo("Équipe Design")
        assertThat(space.description).isEqualTo("Espace partagé")
        assertThat(space.ownerId).isEqualTo(demoProfile.id)
        assertThat(space.visibility).isEqualTo(SpaceVisibility.PRIVATE)
    }

    @Test
    fun `blank name fails without hitting the repository`() = runTest {
        val result = createSpace(name = "   ")

        assertThat(result).isInstanceOf(AppResult.Failure::class.java)
        assertThat((result as AppResult.Failure).error).isInstanceOf(AppError.Unknown::class.java)
    }

    @Test
    fun `fails with NotFound when there is no current profile`() = runTest {
        val noProfileRepository = FakeProfileRepository()
        val useCase = CreateSpaceUseCase(spaceRepository, noProfileRepository)

        val result = useCase(name = "Équipe Design")

        assertThat(result).isInstanceOf(AppResult.Failure::class.java)
        assertThat((result as AppResult.Failure).error).isEqualTo(AppError.NotFound)
    }
}
