package datatouch.uikitapp.adapters

import android.view.View
import android.view.ViewGroup
import datatouch.uikit.components.dropdown.adapter.AutoCompleteDropDownListAdapter

class TestAutocompleteDropDownListAdapter : AutoCompleteDropDownListAdapter<TestItemHolder>() {

    override fun onCreateView(parent: ViewGroup?): View {
        return TestDropDownItemViewUi(parent?.context)
    }

    override fun onBindView(convertView: View, position: Int) {
        val view = convertView as? TestDropDownItemViewUi?
        val item = data[position]
        view?.setupItem(item)
        view?.setItemSelected(selectedItem == item)
    }

}
