package datatouch.uikit.components.dropdown.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull

abstract class DropDownListAdapter<TItem: IDropDownListAdapterItem> : BaseAdapter(),
    Filterable, SelectableAutoCompleteTextViewListAdapter {

    override val isItemSelected get() = selectedItem.isNotNull()

    var selectedItem: TItem? = null

    override var onItemClickCallback: UiCallback<String>? = null
    var onItemSelectionChangeCallback: UiJustCallback? = null

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

    private fun onItemClick(clickedItem: TItem) {
        selectedItem = clickedItem
        onItemClickCallback?.invoke(clickedItem.getString())
        onItemSelectionChangeCallback?.invoke()
    }

    override fun unSelectItem() {
        selectedItem = null
        onItemSelectionChangeCallback?.invoke()
    }

    // For plain dropdown we do not need a filter
    override fun getFilter(): Filter = EmptyFilter()

}