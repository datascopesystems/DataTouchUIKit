package datatouch.uikit.components.panels

import android.content.Context
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import datatouch.uikit.databinding.PopupPanelWithToolbarBinding

abstract class PopUpPanelWithToolbar : PopUpPanel() {

    private var titleText = ""

    override fun inflateInject(context: Context?): ViewBinding? {
        val uiToolbarContainer = createToolbarContainerBinding(LayoutInflater.from(context))
        val uiMain = super.inflateInject(context)

        uiToolbarContainer.root.addView(uiMain?.root)
        return uiToolbarContainer
    }

    private fun createToolbarContainerBinding(inflater: LayoutInflater) =
            PopupPanelWithToolbarBinding.inflate(inflater).also {
                it.btnClose.setOnClickListener { onBtnCloseClick() }
                if (titleText.isNotEmpty()) {
                    it.tvTitle.text = titleText
                }
            }

    private fun onBtnCloseClick() {
        dismiss()
    }

    fun titleText(text: String) = also {
        titleText = text
    }
}