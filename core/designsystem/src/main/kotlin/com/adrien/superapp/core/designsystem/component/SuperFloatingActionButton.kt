package com.adrien.superapp.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.adrien.superapp.core.designsystem.theme.SuperAppShapes
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun SuperFloatingActionButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Add,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = SuperAppShapes.large,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperFloatingActionButtonPreview() {
    SuperAppTheme {
        SuperFloatingActionButton(contentDescription = "Créer", onClick = {})
    }
}
