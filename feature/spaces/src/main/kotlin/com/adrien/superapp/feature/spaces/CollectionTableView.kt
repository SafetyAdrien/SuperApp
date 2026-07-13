package com.adrien.superapp.feature.spaces

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionProperty

/**
 * A real desktop-style grid (sticky columns, per-cell editing) isn't reproduced on a phone-width
 * screen — per the mobile-adaptation rule (docs/FIDELITY.md §7), each row instead shows its
 * visible properties as a horizontally scrollable line of "name: value" chips.
 */
@Composable
fun CollectionTableView(
    entries: List<CollectionEntry>,
    properties: List<CollectionProperty>,
    onEntryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = entries, key = { it.page.id }) { entry ->
            CollectionEntryRow(
                entry = entry,
                properties = properties,
                onClick = { onEntryClick(entry.page.id) },
                showProperties = true,
            )
        }
    }
}
