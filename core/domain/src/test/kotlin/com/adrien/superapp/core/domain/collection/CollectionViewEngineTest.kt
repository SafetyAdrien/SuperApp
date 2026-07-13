package com.adrien.superapp.core.domain.collection

import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import com.adrien.superapp.core.model.FilterOperator
import com.adrien.superapp.core.model.Page
import com.google.common.truth.Truth.assertThat
import org.junit.Test

private const val STATUS_PROPERTY_ID = "prop-status"
private const val DONE_PROPERTY_ID = "prop-done"
private const val SCORE_PROPERTY_ID = "prop-score"

class CollectionViewEngineTest {

    private fun entry(id: String, status: String? = null, done: String? = null, score: String? = null) = CollectionEntry(
        page = Page(id = id, spaceId = "space-1", title = "Entry $id", createdBy = "profile-1", createdAt = 0, updatedAt = 0),
        values = buildMap {
            status?.let { put(STATUS_PROPERTY_ID, it) }
            done?.let { put(DONE_PROPERTY_ID, it) }
            score?.let { put(SCORE_PROPERTY_ID, it) }
        },
    )

    private fun tableView(
        sortPropertyId: String? = null,
        sortDescending: Boolean = false,
        filterPropertyId: String? = null,
        filterOperator: FilterOperator? = null,
        filterValue: String? = null,
    ) = CollectionView(
        id = "view-1",
        collectionId = "collection-1",
        name = "Table",
        type = CollectionViewType.TABLE,
        position = 1000,
        sortPropertyId = sortPropertyId,
        sortDescending = sortDescending,
        filterPropertyId = filterPropertyId,
        filterOperator = filterOperator,
        filterValue = filterValue,
    )

    @Test
    fun `apply with no sort or filter returns entries unchanged`() {
        val entries = listOf(entry("1"), entry("2"))

        val result = CollectionViewEngine.apply(entries, tableView())

        assertThat(result.map { it.page.id }).containsExactly("1", "2").inOrder()
    }

    @Test
    fun `equals filter keeps only matching entries`() {
        val entries = listOf(entry("1", status = "Todo"), entry("2", status = "Done"), entry("3", status = "Todo"))
        val view = tableView(filterPropertyId = STATUS_PROPERTY_ID, filterOperator = FilterOperator.EQUALS, filterValue = "Todo")

        val result = CollectionViewEngine.apply(entries, view)

        assertThat(result.map { it.page.id }).containsExactly("1", "3")
    }

    @Test
    fun `contains filter is case insensitive`() {
        val entries = listOf(entry("1", status = "In Progress"), entry("2", status = "Done"))
        val view = tableView(filterPropertyId = STATUS_PROPERTY_ID, filterOperator = FilterOperator.CONTAINS, filterValue = "progress")

        val result = CollectionViewEngine.apply(entries, view)

        assertThat(result.map { it.page.id }).containsExactly("1")
    }

    @Test
    fun `is checked filter matches only true values`() {
        val entries = listOf(entry("1", done = "true"), entry("2", done = "false"), entry("3"))
        val view = tableView(filterPropertyId = DONE_PROPERTY_ID, filterOperator = FilterOperator.IS_CHECKED)

        val result = CollectionViewEngine.apply(entries, view)

        assertThat(result.map { it.page.id }).containsExactly("1")
    }

    @Test
    fun `is empty filter matches entries with no stored value`() {
        val entries = listOf(entry("1", status = "Todo"), entry("2"))
        val view = tableView(filterPropertyId = STATUS_PROPERTY_ID, filterOperator = FilterOperator.IS_EMPTY)

        val result = CollectionViewEngine.apply(entries, view)

        assertThat(result.map { it.page.id }).containsExactly("2")
    }

    @Test
    fun `sort orders numeric string values numerically not lexically`() {
        val entries = listOf(entry("1", score = "9"), entry("2", score = "10"), entry("3", score = "2"))
        val view = tableView(sortPropertyId = SCORE_PROPERTY_ID)

        val result = CollectionViewEngine.apply(entries, view)

        assertThat(result.map { it.page.id }).containsExactly("3", "1", "2").inOrder()
    }

    @Test
    fun `sort descending reverses order`() {
        val entries = listOf(entry("1", score = "1"), entry("2", score = "3"), entry("3", score = "2"))
        val view = tableView(sortPropertyId = SCORE_PROPERTY_ID, sortDescending = true)

        val result = CollectionViewEngine.apply(entries, view)

        assertThat(result.map { it.page.id }).containsExactly("2", "3", "1").inOrder()
    }

    @Test
    fun `sort treats missing values as empty and sorts them first`() {
        val entries = listOf(entry("1", score = "5"), entry("2"))
        val view = tableView(sortPropertyId = SCORE_PROPERTY_ID)

        val result = CollectionViewEngine.apply(entries, view)

        assertThat(result.map { it.page.id }).containsExactly("2", "1").inOrder()
    }

    @Test
    fun `group buckets entries by their stored value including a null bucket`() {
        val entries = listOf(entry("1", status = "Todo"), entry("2", status = "Done"), entry("3", status = "Todo"), entry("4"))

        val groups = CollectionViewEngine.group(entries, STATUS_PROPERTY_ID)

        assertThat(groups["Todo"]?.map { it.page.id }).containsExactly("1", "3")
        assertThat(groups["Done"]?.map { it.page.id }).containsExactly("2")
        assertThat(groups[null]?.map { it.page.id }).containsExactly("4")
    }
}
