package datatouch.uikit.components.edittext.search

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.databinding.SearchCriterionItemBinding

class SearchCriterionItemUi<TCriterion>(context: Context?) : RelativeLayout(context) {

    private val ui = SearchCriterionItemBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var selectedColor = 0
    private var unselectedColor = 0

    init {
        context?.let { initResources(it) }
    }

    private fun initResources(context: Context) {
        selectedColor = ContextCompat.getColor(context, R.color.accent_end_light)
        unselectedColor = ContextCompat.getColor(context, R.color.white)
    }

    private lateinit var searchCriterionItemHolder: SearchCriterionItemHolder<TCriterion>

    fun setItem(searchCriterionItemHolder: SearchCriterionItemHolder<TCriterion>) {
        this.searchCriterionItemHolder = searchCriterionItemHolder

        ui.ivIcon.setImageResource(searchCriterionItemHolder.iconResId)
        ui.tvName.text = searchCriterionItemHolder.name
    }

    fun setItemSelected(selected: Boolean) {
        if (selected) {
            ui.ivIcon.setBackgroundResource(R.drawable.image_background_circle_accent)
            ui.tvName.setTextColor(selectedColor)
        } else {
            ui.ivIcon.background = null
            ui.tvName.setTextColor(unselectedColor)
        }
    }

}