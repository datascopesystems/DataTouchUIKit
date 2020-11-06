package datatouch.uikit.components.edittext.search

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import kotlinx.android.synthetic.main.search_criterion_item.view.*

class SearchCriterionItemUi<TCriterion>(context: Context?) : RelativeLayout(context) {

    init {
        inflateView()
    }

    private fun inflateView() {
        View.inflate(context, R.layout.search_criterion_item, this)
    }

    private lateinit var searchCriterionItemHolder: SearchCriterionItemHolder<TCriterion>

    fun setItem(searchCriterionItemHolder: SearchCriterionItemHolder<TCriterion>) {
        this.searchCriterionItemHolder = searchCriterionItemHolder

        ivIcon?.setImageResource(searchCriterionItemHolder.iconResId)
        tvName?.text =searchCriterionItemHolder.name
    }

    fun setItemSelected(selected: Boolean) {
        if (selected)
            ivIcon?.setBackgroundResource(R.drawable.image_background_circle_accent)
        else
            ivIcon?.background =null
    }


}