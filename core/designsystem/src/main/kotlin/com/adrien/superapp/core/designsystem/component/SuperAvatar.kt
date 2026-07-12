package com.adrien.superapp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/**
 * Shows the first letter of [name] on a colored background. Image avatars
 * (`imageUrl`) land once a feature actually has one to load — no image
 * loader dependency is pulled into the design system before it's needed.
 */
@Composable
fun SuperAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(SuperAppTheme.extendedColors.info.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
            style = MaterialTheme.typography.titleMedium,
            color = SuperAppTheme.extendedColors.info,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperAvatarPreview() {
    SuperAppTheme {
        SuperAvatar(name = "Alex Demo")
    }
}
