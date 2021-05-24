package datatouch.uikit.components.wizard

import datatouch.uikit.R
import datatouch.uikit.components.windows.base.PopUpWindowUiBind
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.databinding.FragmentQuitWizardBinding

class FQuitWizardConfirmation : PopUpWindowUiBind<FragmentQuitWizardBinding>() {

    var onQuitConfirmedCallback: UiJustCallback? = null

    override fun afterViewCreated() {
        ui?.btnOk?.setOnClickListener {  btnOk() }
    }

    override fun getWindowTitle() = R.string.quit_wizard

    fun btnOk() {
        dismiss()
        onQuitConfirmedCallback?.invoke()
    }

    override fun inject() {}

}