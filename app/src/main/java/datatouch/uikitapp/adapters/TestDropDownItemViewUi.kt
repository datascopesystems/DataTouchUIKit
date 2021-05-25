package datatouch.uikitapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import datatouch.uikit.components.recyclerview.movable.MovableItemView
import datatouch.uikitapp.databinding.TestDropDownItemViewBinding

@SuppressLint("NonConstantResourceId", "ViewConstructor")
class TestDropDownItemViewUi(context: Context?) : MovableItemView(context) {

    private val ui = TestDropDownItemViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    override val moveAnchorView: View get() = ui.iv

    private lateinit var holder: TestItemHolder

    fun setupItem(holder1: TestItemHolder) {
        this.holder = holder1
        ui.tvTitle.text = holder1.name
    }

    fun setItemSelected(selected: Boolean) {
        if (selected)
            ui.tvTitle.setTextColor(Color.BLUE)
        else
            ui.tvTitle.setTextColor(Color.WHITE)
    }
}