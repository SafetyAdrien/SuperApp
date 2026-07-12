package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.designsystem.component.SuperDivider
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme
import com.adrien.superapp.core.model.Page

@Composable
fun PageListItem(
    page: Page,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = SuperAppTheme.spacing.screenHorizontal,
                    vertical = SuperAppTheme.spacing.space12,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Article,
                contentDescription = null,
                tint = SuperAppTheme.extendedColors.textSecondary,
            )
            Text(
                text = page.title,
                style = MaterialTheme.typography.bodyLarge,
                color = SuperAppTheme.extendedColors.textPrimary,
                modifier = Modifier.padding(start = SuperAppTheme.spacing.space12),
            )
        }
        SuperDivider()
    }
}
