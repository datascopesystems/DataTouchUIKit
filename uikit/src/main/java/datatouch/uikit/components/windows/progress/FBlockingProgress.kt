package datatouch.uikit.components.windows.progress

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import datatouch.uikit.R
import datatouch.uikit.components.windows.base.PopUpWindowUiBind
import datatouch.uikit.databinding.DialogBlockingProgressBinding


class FBlockingProgress() : PopUpWindowUiBind<DialogBlockingProgressBinding>() {

    var isDialogCancelable = false

    private var subMessageText = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val root = LinearLayout(windowActivity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(LinearLayout(context))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }

        isCancelable = isDialogCancelable

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {}

    private fun getBlockingProgressRoot(): View? {
        val originalView = getOriginalView()
        val view: View? = originalView?.findViewById(R.id.llBlockingProgressRoot)
        return view
    }

    override fun getView(): View? {
        return getBlockingProgressRoot()
    }

    override fun afterViewCreated() {
        showSubMessage(subMessageText)
    }

    override fun onClose() {
        showSubMessage("")
        super.onClose()
    }

    fun showSubMessage(text: String) {
        subMessageText = text
        ui?.tvSubMessage?.visibility = if (subMessageText.isEmpty()) View.GONE else View.VISIBLE
        ui?.tvSubMessage?.text = subMessageText
    }
}