package com.adrien.superapp.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

data class SuperNavigationItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val contentDescription: String = label,
)

/**
 * Five-destination bottom navigation. The item at [centerItemIndex] (the
 * "Créer" destination) renders as an elevated circular action instead of a
 * regular tab, since it opens a modal sheet rather than navigating.
 */
@Composable
fun SuperBottomNavigation(
    items: List<SuperNavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    centerItemIndex: Int = items.size / 2,
) {
    NavigationBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(index) },
                icon = {
                    if (index == centerItemIndex) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.contentDescription,
                            modifier = Modifier.size(28.dp),
                        )
                    } else {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.contentDescription,
                        )
                    }
                },
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperBottomNavigationPreview() {
    SuperAppTheme {
        SuperBottomNavigation(
            items = listOf(
                SuperNavigationItem("Accueil", androidx.compose.material.icons.Icons.Default.Home),
                SuperNavigationItem("Espaces", androidx.compose.material.icons.Icons.Default.Folder),
                SuperNavigationItem("Créer", androidx.compose.material.icons.Icons.Default.AddCircle),
                SuperNavigationItem("Messages", androidx.compose.material.icons.Icons.AutoMirrored.Filled.Message),
                SuperNavigationItem("Profil", androidx.compose.material.icons.Icons.Default.Person),
            ),
            selectedIndex = 0,
            onItemSelected = {},
        )
    }
}
