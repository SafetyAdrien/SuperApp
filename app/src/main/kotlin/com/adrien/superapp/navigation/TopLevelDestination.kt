package com.adrien.superapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.adrien.superapp.core.navigation.AppRoute

/**
 * The five bottom-navigation destinations (brief §4.1). [Create] has no
 * associated [AppRoute] — selecting it opens the "Créer" bottom sheet
 * instead of navigating.
 */
enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val route: AppRoute?,
) {
    Home("Accueil", Icons.Filled.Home, "Accueil", AppRoute.Home),
    Spaces("Espaces", Icons.Filled.Folder, "Espaces", AppRoute.Spaces),
    Create("Créer", Icons.Filled.AddCircle, "Créer", null),
    Messages("Messages", Icons.AutoMirrored.Filled.Message, "Messages", AppRoute.Messages),
    Profile("Profil", Icons.Filled.Person, "Profil", AppRoute.Profile),
}
