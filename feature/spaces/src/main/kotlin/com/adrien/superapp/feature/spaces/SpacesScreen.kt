package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar

/** Space browser (recent/favorite/personal/shared) lands in Phase 3 with the Space model. */
@Composable
fun SpacesScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(title = "Espaces")
        SuperEmptyState(
            title = "Aucun espace pour l'instant",
            subtitle = "Vos espaces personnels et partagés apparaîtront ici.",
            icon = Icons.Filled.Folder,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
