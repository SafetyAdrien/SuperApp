package com.adrien.superapp.core.designsystem.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adrien.superapp.core.designsystem.theme.SuperAppShapes
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/** The app's primary call-to-action button. Shows a spinner instead of the label while [loading]. */
@Composable
fun SuperPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled && !loading,
        shape = SuperAppShapes.medium,
        colors = ButtonDefaults.buttonColors(),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 0.dp),
                strokeWidth = 2.dp,
            )
        } else {
            Text(text = text)
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperPrimaryButtonPreview() {
    SuperAppTheme {
        SuperPrimaryButton(text = "Continuer", onClick = {})
    }
}

@Preview(name = "Loading")
@Composable
private fun SuperPrimaryButtonLoadingPreview() {
    SuperAppTheme {
        SuperPrimaryButton(text = "Continuer", onClick = {}, loading = true)
    }
}
