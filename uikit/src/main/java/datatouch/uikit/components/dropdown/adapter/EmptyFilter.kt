package datatouch.uikit.components.dropdown.adapter

import android.widget.Filter

class EmptyFilter : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        return FilterResults()
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

    }

}