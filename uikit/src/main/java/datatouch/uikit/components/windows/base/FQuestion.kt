package datatouch.uikit.components.windows.base

import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.databinding.FragmentQuestionBinding


class FQuestion : PopUpWindowUiBind<FragmentQuestionBinding>() {

    var message: String? = null
    var onAcceptConfirmedCallback: UiJustCallback? = null

    override fun afterViewCreated() {
        message?.let { ui?.tvMessage?.text = it }
        ui?.btnOk?.setOnClickListener { btnOk() }
    }

    override fun inject() {}

    override fun getWindowTitle() = R.string.conformation_required

    private fun btnOk() {
        dismiss()
        onAcceptConfirmedCallback?.invoke()
    }
}