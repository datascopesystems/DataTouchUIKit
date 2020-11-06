package datatouch.uikit.components.edittext.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.components.dropdown.adapter.DropDownListAdapter
import datatouch.uikit.core.callbacks.UiCallback

class CriteriaSearchEditTextAdapter<TCriterion> :
    DropDownListAdapter<SearchCriterionItemHolder<TCriterion>>(), ICriteriaSearchEditTextAdapter {

    override var onItemClickCallback: UiCallback<ISearchCriterionDropDownListAdapterItem>? = null
    var selectedItem: TCriterion? = null

    override fun onCreateView(parent: ViewGroup?): View {
        val view = SearchCriterionItemUi<TCriterion>(parent?.context)
        view.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
        return view
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindView(convertView: View, position: Int) {
        val view = convertView as? SearchCriterionItemUi<TCriterion>?
        val item = data[position]
        view?.setItem(item)
        view?.setItemSelected(item.criterion == selectedItem)
    }

    override fun onItemClick(clickedItem: SearchCriterionItemHolder<TCriterion>) {
        selectedItem = clickedItem.criterion
        onItemClickCallback?.invoke(clickedItem)
    }

}