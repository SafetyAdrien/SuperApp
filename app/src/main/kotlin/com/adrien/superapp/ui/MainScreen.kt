package com.adrien.superapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperBottomNavigation
import com.adrien.superapp.core.designsystem.component.SuperBottomSheet
import com.adrien.superapp.core.designsystem.component.SuperNavigationItem
import com.adrien.superapp.core.designsystem.component.SuperOfflineBanner
import com.adrien.superapp.core.navigation.AppRoute
import com.adrien.superapp.feature.create.CreateAction
import com.adrien.superapp.feature.create.CreateSheetContent
import com.adrien.superapp.navigation.SuperAppNavHost
import com.adrien.superapp.navigation.TopLevelDestination
import com.adrien.superapp.navigation.rememberAppState
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val appState = rememberAppState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showCreateSheet by rememberSaveable { mutableStateOf(false) }

    val currentTopLevel = appState.currentTopLevelDestination ?: TopLevelDestination.Home
    val destinations = TopLevelDestination.entries

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            SuperBottomNavigation(
                items = destinations.map {
                    SuperNavigationItem(label = it.label, icon = it.icon, contentDescription = it.contentDescription)
                },
                selectedIndex = destinations.indexOf(currentTopLevel),
                onItemSelected = { index ->
                    when (val destination = destinations[index]) {
                        TopLevelDestination.Create -> showCreateSheet = true
                        else -> appState.navigateToTopLevelDestination(destination)
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SuperOfflineBanner(visible = !isOnline)
            SuperAppNavHost(appState = appState, modifier = Modifier.weight(1f))
        }
    }

    if (showCreateSheet) {
        SuperBottomSheet(onDismissRequest = { showCreateSheet = false }) {
            CreateSheetContent(
                onAction = { action ->
                    showCreateSheet = false
                    if (action == CreateAction.NEW_POST) {
                        appState.navController.navigate(AppRoute.ComposePost())
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("${action.label} — bientôt disponible")
                        }
                    }
                },
            )
        }
    }
}
