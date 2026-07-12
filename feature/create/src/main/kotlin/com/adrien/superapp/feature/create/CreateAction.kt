package com.adrien.superapp.feature.create

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.ui.graphics.vector.ImageVector

/** The eight creation shortcuts the central "Créer" sheet offers (brief §4.1). */
enum class CreateAction(val label: String, val icon: ImageVector) {
    NEW_POST("Nouvelle publication", Icons.Filled.Edit),
    NEW_PAGE("Nouvelle page", Icons.AutoMirrored.Filled.Article),
    NEW_NOTE("Nouvelle note", Icons.Filled.StickyNote2),
    NEW_TASK("Nouvelle tâche", Icons.Filled.CheckCircle),
    NEW_PROJECT("Nouveau projet", Icons.Filled.Folder),
    NEW_MESSAGE("Nouveau message", Icons.AutoMirrored.Filled.Message),
    NEW_SPACE("Nouvel espace", Icons.Filled.Groups),
    NEW_CANVAS("Nouveau canvas", Icons.Filled.Draw),
}
