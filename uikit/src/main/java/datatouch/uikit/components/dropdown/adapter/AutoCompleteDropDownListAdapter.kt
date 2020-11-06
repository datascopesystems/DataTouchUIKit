package datatouch.uikit.components.dropdown.adapter

import android.widget.Filter
import datatouch.uikit.components.recyclerview.ListFilter

abstract class AutoCompleteDropDownListAdapter<TItem : IDropDownListAdapterItem>
    : SelectableDropDownListAdapter<TItem>() {

    private val listFilter =
        ListFilter<DropDownListFilterItem<TItem>, DefaultDropDownListCriterion>()

    override var data: MutableList<TItem>
        get() = super.data
        set(data) {
            super.data = data
            listFilter.allData = data.map { DropDownListFilterItem(it) }.toMutableList()
            notifyDataSetChanged()
        }

    private val filterObject = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = listFilter.invoke(
                constraint.toString(),
                DefaultDropDownListCriterion.Default
            )
            val filterResults = FilterResults()
            filterResults.values = filteredList
            filterResults.count = filteredList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            @Suppress("UNCHECKED_CAST")
            val filteredItems = results?.values
                    as? MutableList<DropDownListFilterItem<TItem>>? ?: mutableListOf()

            super@AutoCompleteDropDownListAdapter.data =
                filteredItems.map { it.item }.toMutableList()

            notifyDataSetChanged()
        }
    }

    override fun getFilter() = filterObject

}