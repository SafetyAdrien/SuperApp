package com.adrien.superapp.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/**
 * Icon-only button. [contentDescription] is required (non-null) — this
 * component cannot be used to ship an icon button without an accessible
 * label.
 */
@Composable
fun SuperIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperIconButtonPreview() {
    SuperAppTheme {
        SuperIconButton(
            icon = androidx.compose.material.icons.Icons.Default.Search,
            contentDescription = "Rechercher",
            onClick = {},
        )
    }
}
