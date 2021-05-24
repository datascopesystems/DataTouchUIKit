package datatouch.uikit.components.windows.base

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import datatouch.uikit.R
import datatouch.uikit.components.windows.base.toolbars.PopUpWindowToolbar
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.GenericExtensions.default


abstract class PopUpWindow : FragmentWindow() {

    override val rootView: View? by lazy {
        LayoutInflater.from(context).inflate(rootLayoutRes, null, false)
    }

    var onBackClickedCallback: UiJustCallback? = null

    open val customTitle: String? = null

    protected val title: String
        get() = customTitle.default(if (getWindowTitle() == 0) ""
        else (context?.getString(getWindowTitle()).default("")))

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).also {
            val root = LinearLayout(requireContext())
            root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            it.window?.apply {
                setDimAmount(0f)
                setWindowAnimations(R.style.PopUpWindowAnimations)
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(LinearLayout(context))
                setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }

            it.setOnDismissListener { onClose() }
            it.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (event.action == KeyEvent.ACTION_UP) {
                        onNavigationBackPress()
                        if (isCancelable) {
                            onClose()
                            dismiss()
                        }
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupMinimumDialogWidth()
    }

    private fun setupMinimumDialogWidth() {
        val widthCoeff = 0.7f

        windowActivity?.let {
            val displayMetrics = it.resources.displayMetrics
            val screenWidthPx = displayMetrics.widthPixels
            val window = dialog?.window
            val params = window?.attributes
            if (params?.width != WindowManager.LayoutParams.MATCH_PARENT) {
                params?.width = (widthCoeff * screenWidthPx).toInt()
                window?.attributes = params
            }
        }

        dialog?.window?.setGravity(Gravity.CENTER)
    }

    override fun onCancel(dialog: DialogInterface) {
        onClose()
        super.onCancel(dialog)
    }

    private val toolBar: PopUpWindowToolbar?
        get() = rootView?.findViewById(R.id.toolbar)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val llContainer = rootView?.findViewById<LinearLayout>(R.id.llContainer)
        view.parent?.apply { (this as ViewGroup?)?.removeView(view) }
        llContainer?.addView(view)

        toolBar?.apply {
            setIcon(getIconDrawable())
            setTitle(title)
            setOnBackButtonListener {
                onClose()
                dismiss()
            }
        }


        rootView?.apply {
            super.onViewCreated(this, savedInstanceState)
        }
    }

    @get:LayoutRes
    protected open val rootLayoutRes
        get() = R.layout.dialog_window_fragment

    override fun onClose() {
        onBackClickedCallback?.invoke()
    }

    protected open fun getWindowTitle() = 0

    protected open fun getIconDrawable(): Drawable? = null

    override fun getView(): View? {
        return rootView
    }

    protected fun getOriginalView(): View? {
        return super.getView()
    }

    override fun dismiss() {
        super.dismiss()
        onClose()
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)

        // Run only with runCatching, otherwise crashes when called before views created
        runCatching { toolBar?.setCloseButtonVisibility(cancelable) }
    }

}