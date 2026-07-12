package com.adrien.superapp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/** Used for both "nothing here yet" and (via [SuperErrorState] later) failure states. */
@Composable
fun SuperEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SuperAppTheme.spacing.space24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SuperAppTheme.extendedColors.textTertiary,
                modifier = Modifier.padding(bottom = SuperAppTheme.spacing.space16),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = SuperAppTheme.extendedColors.textPrimary,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = SuperAppTheme.extendedColors.textSecondary,
                modifier = Modifier.padding(top = SuperAppTheme.spacing.space8),
            )
        }
        if (action != null) {
            Spacer(Modifier.height(SuperAppTheme.spacing.space16))
            action()
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperEmptyStatePreview() {
    SuperAppTheme {
        SuperEmptyState(
            title = "Rien pour l'instant",
            subtitle = "Ce contenu arrive dans une prochaine phase.",
        )
    }
}
