package datatouch.uikit.components.windows.error

import datatouch.uikit.R
import datatouch.uikit.components.windows.base.PopUpWindowUiBind
import datatouch.uikit.databinding.QaFragmentErrorBinding

class FQaErrorNotification : PopUpWindowUiBind<QaFragmentErrorBinding>() {

    var errorMessage: String? = null

    override fun afterViewCreated() {
        errorMessage?.let { ui?.tvMessage?.text = it }
        ui?.btnOk?.setOnClickListener { btnOk() }
    }

    override fun getWindowTitle() = R.string.error

    fun btnOk() {
        dismiss()
    }

}