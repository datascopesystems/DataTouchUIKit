package datatouch.uikit.components.dropdown.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListAdapter

abstract class DropDownListAdapter<TItem : IDropDownListAdapterItem>
    : BaseAdapter(), ListAdapter, Filterable {

    private var dataList: MutableList<TItem> = mutableListOf()
    open var data: MutableList<TItem>
        get() = dataList
        set(data) {
            dataList = data
            notifyDataSetChanged()
        }

    override fun getCount() = data.size

    override fun getItem(position: Int) = data[position]

    override fun getItemId(position: Int) = position.toLong()

    abstract fun onCreateView(parent: ViewGroup?): View

    abstract fun onBindView(convertView: View, position: Int)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null)
            view = onCreateView(parent)

        onBindView(view, position)
        view.setOnClickListener { onItemClick(data[position]) }
        return view
    }

    protected open fun onItemClick(clickedItem: TItem) {}

    // For plain dropdown we do not need a filter
    override fun getFilter(): Filter = EmptyFilter()

}