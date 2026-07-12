package com.adrien.superapp.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.adrien.superapp.core.designsystem.theme.SuperAppShapes
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

@Composable
fun SuperTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    label: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        isError = isError,
        singleLine = singleLine,
        minLines = minLines,
        shape = SuperAppShapes.medium,
        label = label?.let { { Text(text = it) } },
        placeholder = placeholder?.let { { Text(text = it) } },
        supportingText = supportingText?.let { { Text(text = it) } },
        colors = OutlinedTextFieldDefaults.colors(),
        textStyle = textStyle,
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperTextFieldPreview() {
    SuperAppTheme {
        SuperTextField(
            value = "Bonjour Super App",
            onValueChange = {},
            label = "Publication",
            placeholder = "Quoi de neuf ?",
        )
    }
}
