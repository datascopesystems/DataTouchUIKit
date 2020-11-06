package datatouch.uikit.components.dropdown.adapter

import android.widget.Filterable
import android.widget.ListAdapter
import datatouch.uikit.core.callbacks.UiCallback

interface ISelectableDropDownListAdapter : ListAdapter, Filterable {

    val isItemSelected: Boolean
    var onItemClickCallback : UiCallback<IDropDownListAdapterItem>?
    fun unSelectItem()

}