package com.adrien.superapp.feature.messages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperEmptyState
import com.adrien.superapp.core.designsystem.component.SuperTopAppBar

/** Conversation list (direct/group/channel) lands in Phase 6 with the Conversation/Message models. */
@Composable
fun MessagesScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SuperTopAppBar(title = "Messages")
        SuperEmptyState(
            title = "Aucune conversation",
            subtitle = "Vos messages directs, groupes et canaux apparaîtront ici.",
            icon = Icons.AutoMirrored.Filled.Message,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
