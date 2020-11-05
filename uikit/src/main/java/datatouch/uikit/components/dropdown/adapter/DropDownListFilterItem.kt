package datatouch.uikit.components.dropdown.adapter

import datatouch.uikit.components.recyclerview.IListAdapterFilterable

data class DropDownListFilterItem<TItem: IDropDownListAdapterItem>(val item : TItem)
    : IListAdapterFilterable<DefaultDropDownListCriterion> {

    override fun getSearchString(criterion: DefaultDropDownListCriterion): String {
        return when (criterion) {
            DefaultDropDownListCriterion.Default -> item.getString()
        }
    }

}