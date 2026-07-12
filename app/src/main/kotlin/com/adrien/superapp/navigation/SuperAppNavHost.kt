package com.adrien.superapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adrien.superapp.core.navigation.AppRoute
import com.adrien.superapp.feature.home.HomeScreen
import com.adrien.superapp.feature.messages.MessagesScreen
import com.adrien.superapp.feature.profile.ProfileScreen
import com.adrien.superapp.feature.settings.SettingsScreen
import com.adrien.superapp.feature.spaces.SpacesScreen

@Composable
fun SuperAppNavHost(
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = appState.navController,
        startDestination = AppRoute.Home,
        modifier = modifier,
    ) {
        composable<AppRoute.Home> { HomeScreen() }
        composable<AppRoute.Spaces> { SpacesScreen() }
        composable<AppRoute.Messages> { MessagesScreen() }
        composable<AppRoute.Profile> {
            ProfileScreen(onOpenSettings = { appState.navController.navigate(AppRoute.Settings) })
        }
        composable<AppRoute.Settings> {
            SettingsScreen(onBackClick = { appState.navController.popBackStack() })
        }
    }
}
