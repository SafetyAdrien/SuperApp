package com.adrien.superapp.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar

/**
 * Feed shell (Pour vous / Abonnements / Activité tabs land in Phase 2 with
 * Post/Reaction — see docs/PRODUCT.md).
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(title = "Super App")
        SuperEmptyState(
            title = "Votre fil arrive bientôt",
            subtitle = "Les publications et l'activité de vos espaces s'afficheront ici.",
            icon = Icons.AutoMirrored.Filled.Article,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
