package datatouch.uikit.components.recyclerview

import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewFilterableListAdapter<TItem : IListAdapterFilterable<TCriterion>,
        TCriterion, VH : RecyclerView.ViewHolder>
    : RecyclerViewListAdapter<TItem, VH>() {

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

}