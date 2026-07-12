package com.adrien.superapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adrien.superapp.core.navigation.AppRoute
import com.adrien.superapp.feature.home.ComposePostScreen
import com.adrien.superapp.feature.home.HomeScreen
import com.adrien.superapp.feature.home.PostDetailScreen
import com.adrien.superapp.feature.messages.MessagesScreen
import com.adrien.superapp.feature.profile.ProfileScreen
import com.adrien.superapp.feature.settings.SettingsScreen
import com.adrien.superapp.feature.spaces.SpacesScreen

@Composable
fun SuperAppNavHost(
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
        modifier = modifier,
    ) {
        composable<AppRoute.Home> {
            HomeScreen(
                onPostClick = { postId -> navController.navigate(AppRoute.PostDetail(postId)) },
                onComposeClick = { navController.navigate(AppRoute.ComposePost()) },
            )
        }
        composable<AppRoute.Spaces> { SpacesScreen() }
        composable<AppRoute.Messages> { MessagesScreen() }
        composable<AppRoute.Profile> {
            ProfileScreen(onOpenSettings = { navController.navigate(AppRoute.Settings) })
        }
        composable<AppRoute.Settings> {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable<AppRoute.PostDetail> {
            PostDetailScreen(
                onBackClick = { navController.popBackStack() },
                onReplyClick = { replyId -> navController.navigate(AppRoute.PostDetail(replyId)) },
            )
        }
        composable<AppRoute.ComposePost> {
            ComposePostScreen(
                onBackClick = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() },
            )
        }
    }
}
