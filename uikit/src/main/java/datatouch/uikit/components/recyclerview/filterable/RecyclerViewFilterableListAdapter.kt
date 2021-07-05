package datatouch.uikit.components.recyclerview.filterable

import android.view.View
import datatouch.uikit.components.recyclerview.RecyclerViewListAdapter

abstract class RecyclerViewFilterableListAdapter<TItem : IListAdapterFilterable<TCriterion>,
        TCriterion, TView : View>
    : RecyclerViewListAdapter<TItem, TView>() {

    override var data: List<TItem>
        get() = super.data
        set(data) {
            filter.allData = data.toMutableList()
            super.data = data.toMutableList()
        }

    private var filter = ListFilter<TItem, TCriterion>()

    fun filter(constraint: String, criterion: TCriterion) {
        super.data = filter.invoke(constraint, criterion)
    }

    val notFilteredData get() = filter.allData

    override fun replaceListDataOnly(newData: List<TItem>) {
        super.data = newData.toMutableList()
    }
}