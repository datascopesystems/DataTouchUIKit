package datatouch.uikit.components.dropdown.adapter

import android.widget.Filterable
import android.widget.ListAdapter
import datatouch.uikit.core.callbacks.UiCallback

interface SelectableAutoCompleteTextViewListAdapter : ListAdapter, Filterable {

    val isItemSelected: Boolean
    var onItemClickCallback : UiCallback<String>?
    fun unSelectItem()

}