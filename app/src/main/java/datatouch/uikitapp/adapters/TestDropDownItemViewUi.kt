package datatouch.uikitapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import datatouch.uikitapp.R
import kotlinx.android.synthetic.main.test_drop_down_item_view.view.*

@SuppressLint("NonConstantResourceId", "ViewConstructor")
class TestDropDownItemViewUi(context: Context?) : LinearLayout(context) {

    init {
        View.inflate(context, R.layout.test_drop_down_item_view, this)
    }

    private lateinit var holder: TestItemHolder

    fun setupItem(holder1: TestItemHolder) {
        this.holder = holder1
        tvTitle?.text = holder1.name
    }

    fun setItemSelected(selected: Boolean) {
        if (selected)
            tvTitle?.setTextColor(Color.BLUE)
        else
            tvTitle?.setTextColor(Color.WHITE)
    }
}