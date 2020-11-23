package datatouch.uikit.components.dropdown.adapter

import android.widget.Filter
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull

abstract class SelectableDropDownListAdapter<TItem : IDropDownListAdapterItem>
    : DropDownListAdapter<TItem>(), ISelectableDropDownListAdapter {

    var selectedItem: TItem? = null
        private set

    override val isItemSelected get() = selectedItem.isNotNull()
    override var onViewInvalidateRequiredCallback: UiCallback<IDropDownListAdapterItem?>? = null
    override var onItemClickCallback: UiCallback<IDropDownListAdapterItem>? = null

    override fun onItemClick(clickedItem: TItem) {
        selectedItem = clickedItem
        onItemClickCallback?.invoke(clickedItem)
        onViewInvalidateRequiredCallback?.invoke(selectedItem)
    }

    fun selectItem(item: TItem) {
        selectedItem = item
        onViewInvalidateRequiredCallback?.invoke(selectedItem)
    }

    override fun unSelectItem() {
        selectedItem = null
        onViewInvalidateRequiredCallback?.invoke(null)
    }

    // For plain dropdown we do not need a filter
    override fun getFilter(): Filter = EmptyFilter()

}