package datatouch.uikit.components.windows.base.toolbars

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.databinding.FullScreenWindowToolbarBinding


@SuppressLint("ViewConstructor")
class DefaultFullScreenWindowToolbar(context: Context?)
    : RelativeLayout(context) {

    var titleText = ""
        set(value) {
            field = value
            setupTitle()
        }

    private val ui = FullScreenWindowToolbarBinding
        .inflate(LayoutInflater.from(context), this, true)

    init {
        setupMarginsAndPaddings()
    }

    private fun setupMarginsAndPaddings() {
        ui.rootToolbar.setContentInsetsRelative(0, 0)
        ui.rootToolbar.contentInsetStartWithNavigation = 0
        ui.rootToolbar.setContentInsetsAbsolute(0, 0)
        ui.rootToolbar.contentInsetEndWithActions = 0
        ui.rootToolbar.setPadding(0, 0, 0, 0)
        setPadding(0, 0, 0, 0)
    }

    private fun setupTitle() { ui.tvTitle.text = titleText }

    fun setOnBackButtonListener(clickListener: OnClickListener?) {
        ui.btnBack.setOnClickListener(clickListener)
    }

    fun addViewToButtonsContainer(view: View) = ui.llButtonsContainer.addView(view)

    fun setTitle(titleText: String?) {
        this.titleText = titleText.orEmpty()
        setupTitle()
    }

}