package datatouch.uikit.components.edittext.search

import android.widget.Filterable
import android.widget.ListAdapter
import datatouch.uikit.core.callbacks.UiCallback

interface ICriteriaSearchEditTextAdapter : ListAdapter, Filterable {

    var onItemSelectionChangeCallback: UiCallback<ISearchCriterionDropDownListAdapterItem>?

}