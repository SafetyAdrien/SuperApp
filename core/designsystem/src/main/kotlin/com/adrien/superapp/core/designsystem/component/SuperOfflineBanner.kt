package com.adrien.superapp.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/**
 * Shown when [ConnectivityObserver][com.adrien.superapp.core.common.connectivity.ConnectivityObserver]
 * reports the device is offline. Pairs color with an icon + text so the state
 * isn't conveyed by color alone.
 */
@Composable
fun SuperOfflineBanner(
    visible: Boolean,
    modifier: Modifier = Modifier,
    text: String = "Vous êtes hors ligne",
) {
    AnimatedVisibility(visible = visible, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SuperAppTheme.extendedColors.warning.copy(alpha = 0.16f))
                .padding(
                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                    vertical = SuperAppTheme.spacing.space8,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = null,
                tint = SuperAppTheme.extendedColors.warning,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = SuperAppTheme.extendedColors.textPrimary,
                modifier = Modifier.padding(start = SuperAppTheme.spacing.space8),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperOfflineBannerPreview() {
    SuperAppTheme { SuperOfflineBanner(visible = true) }
}
