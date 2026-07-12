package com.adrien.superapp.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun SuperLoadingIndicator(
    modifier: Modifier = Modifier,
    contentDescription: String = "Chargement en cours",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(SuperAppTheme.spacing.space24),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { this.contentDescription = contentDescription },
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperLoadingIndicatorPreview() {
    SuperAppTheme { SuperLoadingIndicator() }
}
