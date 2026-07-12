package com.adrien.superapp.feature.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/**
 * Content of the central "Créer" bottom sheet. This composable only reports
 * which [CreateAction] was tapped — [MainScreen][com.adrien.superapp.ui]
 * decides what happens next. As of Phase 2, [CreateAction.NEW_POST] opens
 * the real composer; the other seven show a "bientôt disponible" snackbar
 * since their owning feature hasn't landed yet.
 */
@Composable
fun CreateSheetContent(
    onAction: (CreateAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.padding(bottom = SuperAppTheme.spacing.space24)) {
        items(CreateAction.entries) { action ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClickLabel = action.label) { onAction(action) }
                    .semantics {
                        contentDescription = action.label
                        role = Role.Button
                    }
                    .padding(
                        horizontal = SuperAppTheme.spacing.screenHorizontal,
                        vertical = SuperAppTheme.spacing.space16,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = SuperAppTheme.extendedColors.textSecondary,
                )
                Text(
                    text = action.label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SuperAppTheme.extendedColors.textPrimary,
                    modifier = Modifier.padding(start = SuperAppTheme.spacing.space16),
                )
            }
        }
    }
}
