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

    override fun updateData(item: TItem) {
        super.updateData(item)

        filter.allData.firstOrNull { it == item }?.apply {
            val mutableData = filter.allData.toMutableList()
            val i = mutableData.indexOf(this)
            kotlin.runCatching {
                mutableData[i] = item
                filter.allData = mutableData
            }
        }
    }

    override fun addData(position: Int, item: TItem) {
        super.addData(position, item)

        if (position <= filter.allData.size) {
            val mutableData = filter.allData.toMutableList()
            mutableData.add(position, item)
            filter.allData = mutableData.toMutableList()
        }
    }


    override fun removeData(position: Int) {
        super.removeData(position)

        if (position < filter.allData.size) {
            val mutableData = filter.allData.toMutableList()
            mutableData.removeAt(position)
            filter.allData = mutableData.toMutableList()
        }

    }


}