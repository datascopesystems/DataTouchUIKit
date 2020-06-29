package datatouch.uikit.components.floatingrevealmenu.view

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class DynamicGridLayoutManager internal constructor(
    context: Context?,
    private val minItemWidth: Int,
    private var totalItems: Int
) : GridLayoutManager(context, 1) {
    override fun onLayoutChildren(
        recycler: Recycler,
        state: RecyclerView.State
    ) {
        updateSpanCount()
        super.onLayoutChildren(recycler, state)
    }

    fun updateTotalItems(totalItems: Int) {
        this.totalItems = totalItems
    }

    private fun updateSpanCount() {
        var spanCount = 1
        if (minItemWidth != 0) {
            spanCount = Math.min(width / minItemWidth, totalItems)
            if (spanCount < 1) spanCount = 1
        }
        spanCount = spanCount
    }

}