package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.model.EntityType
import com.adrien.superapp.core.model.Profile
import com.adrien.superapp.core.testing.repository.FakeProfileRepository
import com.adrien.superapp.core.testing.repository.FakeReactionRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleReactionUseCaseTest {

    private val reactionRepository = FakeReactionRepository()
    private val profileRepository = FakeProfileRepository()
    private lateinit var toggleReaction: ToggleReactionUseCase

    private val demoProfile = Profile(
        id = "profile-1",
        handle = "alex.demo",
        displayName = "Alex Demo",
        createdAt = 0,
        updatedAt = 0,
    )

    @Before
    fun setUp() {
        toggleReaction = ToggleReactionUseCase(reactionRepository, profileRepository)
        profileRepository.setCurrentProfile(demoProfile)
    }

    @Test
    fun `toggling twice returns to the un-reacted state`() = runTest {
        toggleReaction(entityType = EntityType.POST, entityId = "post-1")
        assertThat(
            reactionRepository.hasReacted(EntityType.POST, "post-1", demoProfile.id, "like"),
        ).isTrue()

        toggleReaction(entityType = EntityType.POST, entityId = "post-1")
        assertThat(
            reactionRepository.hasReacted(EntityType.POST, "post-1", demoProfile.id, "like"),
        ).isFalse()
    }
}
