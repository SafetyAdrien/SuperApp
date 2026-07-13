package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.model.CollectionEntry

@Composable
fun CollectionListView(
    entries: List<CollectionEntry>,
    onEntryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = entries, key = { it.page.id }) { entry ->
            CollectionEntryRow(
                entry = entry,
                properties = emptyList(),
                onClick = { onEntryClick(entry.page.id) },
                showProperties = false,
            )
        }
    }
}
