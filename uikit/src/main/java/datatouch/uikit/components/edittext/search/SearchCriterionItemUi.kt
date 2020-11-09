package datatouch.uikit.components.edittext.search

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import kotlinx.android.synthetic.main.search_criterion_item.view.*

class SearchCriterionItemUi<TCriterion>(context: Context?) : RelativeLayout(context) {

    private var selectedColor = 0
    private var unselectedColor = 0

    init {
        inflateView()
        context?.let { initResources(it) }
    }

    private fun inflateView() {
        View.inflate(context, R.layout.search_criterion_item, this)
    }

    private fun initResources(context: Context) {
        selectedColor = ContextCompat.getColor(context, R.color.accent_end_light)
        unselectedColor = ContextCompat.getColor(context, R.color.white)
    }

    private lateinit var searchCriterionItemHolder: SearchCriterionItemHolder<TCriterion>

    fun setItem(searchCriterionItemHolder: SearchCriterionItemHolder<TCriterion>) {
        this.searchCriterionItemHolder = searchCriterionItemHolder

        ivIcon?.setImageResource(searchCriterionItemHolder.iconResId)
        tvName?.text = searchCriterionItemHolder.name
    }

    fun setItemSelected(selected: Boolean) {
        if (selected) {
            ivIcon?.setBackgroundResource(R.drawable.image_background_circle_accent)
            tvName?.setTextColor(selectedColor)
        } else {
            ivIcon?.background = null
            tvName?.setTextColor(unselectedColor)
        }
    }

}