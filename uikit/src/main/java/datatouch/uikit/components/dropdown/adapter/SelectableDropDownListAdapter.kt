package datatouch.uikit.components.dropdown.adapter

import android.widget.Filter
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull

abstract class SelectableDropDownListAdapter<TItem : IDropDownListAdapterItem>
    : DropDownListAdapter<TItem>(), ISelectableDropDownListAdapter {

    var selectedItem: TItem? = null
        private set

    var onItemSelectionChangeCallback: UiJustCallback? = null

    override val isItemSelected get() = selectedItem.isNotNull()
    override var onItemClickCallback: UiCallback<IDropDownListAdapterItem>? = null

    override fun onItemClick(clickedItem: TItem) {
        selectedItem = clickedItem
        onItemClickCallback?.invoke(clickedItem)
        onItemSelectionChangeCallback?.invoke()
    }

    fun selectItem(item: TItem) {
        onItemClick(item)
    }

    override fun unSelectItem() {
        selectedItem = null
        onItemSelectionChangeCallback?.invoke()
    }

    // For plain dropdown we do not need a filter
    override fun getFilter(): Filter = EmptyFilter()

}