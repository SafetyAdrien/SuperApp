package com.adrien.superapp.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperIconButton
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar

/** Own-profile screen (avatar/bio/posts/spaces/stats) lands in Phase 8 with the Profile model. */
@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(
            title = "Profil",
            actions = {
                SuperIconButton(
                    icon = Icons.Filled.Settings,
                    contentDescription = "Paramètres",
                    onClick = onOpenSettings,
                )
            },
        )
        SuperEmptyState(
            title = "Profil de démonstration",
            subtitle = "Votre profil, vos publications et vos espaces publics apparaîtront ici.",
            icon = Icons.Filled.Person,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
