package com.sysco.starwars.ui.components.loading

import androidx.compose.foundation.lazy.LazyListLayoutInfo

interface LoadMoreLogic {
    fun shouldLoadMore(layoutInfo: LazyListLayoutInfo): Boolean
}

class LoadMoreLogicImpl(private val boundary: Int = 8) : LoadMoreLogic {
    override fun shouldLoadMore(layoutInfo: LazyListLayoutInfo): Boolean {
        val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return false
        val totalItems = layoutInfo.totalItemsCount

        return totalItems > 0 && lastVisibleItem.index >= totalItems - boundary
    }
}
