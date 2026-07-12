package com.adrien.superapp.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.adrien.superapp.core.designsystem.theme.SuperAppTheme

/**
 * Contextual top bar: pass only the [actions]/[navigationIcon] a given screen
 * actually needs — screens should not all show the same action set.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    backContentDescription: String = "Retour",
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = backContentDescription,
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = 0x20)
@Composable
private fun SuperTopAppBarPreview() {
    SuperAppTheme {
        SuperTopAppBar(title = "Espaces", onBackClick = {})
    }
}
