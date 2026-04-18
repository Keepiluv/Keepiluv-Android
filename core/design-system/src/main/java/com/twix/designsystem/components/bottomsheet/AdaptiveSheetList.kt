package com.twix.designsystem.components.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> AdaptiveSheetList(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    items: List<T>,
    key: ((T) -> Any)? = null,
    threshold: Int = 12,
    itemContent: @Composable (T) -> Unit,
) {
    if (items.size <= threshold) {
        val scroll = rememberScrollState()

        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll),
            verticalArrangement = verticalArrangement,
        ) {
            items.forEach { item ->
                itemContent(item)
            }
        }
    } else {
        val state = rememberLazyListState()

        LazyColumn(
            state = state,
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = verticalArrangement,
        ) {
            if (key != null) {
                items(items, key = { key(it) }) { itemContent(it) }
            } else {
                items(items) { itemContent(it) }
            }
        }
    }
}
