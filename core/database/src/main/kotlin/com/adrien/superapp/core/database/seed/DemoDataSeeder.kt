package com.adrien.superapp.core.database.seed

import com.adrien.superapp.core.database.dao.PostDao
import com.adrien.superapp.core.database.dao.ProfileDao
import com.adrien.superapp.core.database.entity.PostEntity
import com.adrien.superapp.core.database.entity.ProfileEntity
import com.adrien.superapp.core.model.PostVisibility
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds `DataEnvironment.LOCAL_DEMO` content the first time the app runs —
 * never overwrites existing rows (brief §11: "only when the local database
 * is empty").
 */
@Singleton
class DemoDataSeeder @Inject constructor(
    private val profileDao: ProfileDao,
    private val postDao: PostDao,
) {
    suspend fun seedIfEmpty() {
        if (profileDao.count() > 0) return

        val now = System.currentTimeMillis()
        val demoProfile = ProfileEntity(
            id = UUID.randomUUID().toString(),
            handle = "alex.demo",
            displayName = "Alex Demo",
            biography = "Compte de démonstration locale de Super App.",
            avatarUrl = null,
            bannerUrl = null,
            createdAt = now,
            updatedAt = now,
        )
        profileDao.insert(demoProfile)

        val demoTexts = listOf(
            "Bienvenue sur Super App — tout ce que vous voyez ici fonctionne hors connexion.",
            "Vous pouvez créer une publication, une page, une tâche ou un espace depuis le bouton Créer.",
            "Cette publication de démonstration peut recevoir une réaction — essayez le cœur ci-dessous.",
            "Les espaces réunissent pages, projets et discussions dans un même endroit.",
            "L'éditeur de blocs arrive dans une prochaine phase — restez à l'écoute.",
            "Toutes les données de démonstration sont locales : rien n'est envoyé à un serveur.",
        )
        val demoPosts = demoTexts.mapIndexed { index, text ->
            val createdAt = now - (demoTexts.size - index) * 60_000L
            PostEntity(
                id = UUID.randomUUID().toString(),
                authorId = demoProfile.id,
                spaceId = null,
                text = text,
                replyToPostId = null,
                quotedPostId = null,
                visibility = PostVisibility.PUBLIC.name,
                createdAt = createdAt,
                updatedAt = createdAt,
            )
        }
        postDao.insertAll(demoPosts)
    }
}
