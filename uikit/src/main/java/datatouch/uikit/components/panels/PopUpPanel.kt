package datatouch.uikit.components.panels

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import datatouch.uikit.R
import datatouch.uikit.components.windows.base.UiBindingInjector
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.GenericExtensions.default
import kotlin.reflect.KProperty


abstract class PopUpPanel {

    private var context: Context? = null
    private var popupWindow: PopupWindow? = null
    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED

    private val uiBindingInjector = UiBindingInjector()
    protected abstract val uiBindingProperty: KProperty<ViewBinding?>

    protected fun setKeyboardHidden(hidden: Boolean) {
        softInputMode = when (hidden) {
            true -> WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            else -> WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
        }
        popupWindow?.softInputMode = softInputMode
    }

    private fun createPopup(parentView: View?, callback: UiJustCallback) {
        this.context = parentView?.context
        if (popupWindow == null) {
            setupPopupWindow(context)
            onCreationComplete()
            callback.invoke()
        }
    }

    private fun setupPopupWindow(context: Context?) {
        setKeyboardHidden(true)
        val ui = inflateInject(context)
        val rootView = provideRootView(ui)
        val width = rootView?.layoutParams?.width.default(ViewGroup.LayoutParams.WRAP_CONTENT)
        val height = rootView?.layoutParams?.height.default(ViewGroup.LayoutParams.WRAP_CONTENT)

        popupWindow = PopupWindow(context).also {
            it.width = width
            it.height = height

            it.isOutsideTouchable = true
            it.isFocusable = true

            it.setBackgroundDrawable(getBackgroundDrawable())
            it.contentView = rootView

            it.animationStyle = android.R.style.Animation_Dialog

            it.setOnDismissListener(::onDismissInternal)

            it.softInputMode = softInputMode
            it.isClippingEnabled = false
        }
    }

    private fun onDismissInternal() {
        uiBindingInjector.releaseInjected()
        context = null
        popupWindow?.setOnDismissListener(null)
        popupWindow?.contentView = null
        popupWindow = null
    }

    @get:DrawableRes
    protected open val backgroundDrawableRes
        get() = R.drawable.round_shadow_background

    private fun getBackgroundDrawable(): Drawable? {
        return if (backgroundDrawableRes != 0) {
            getDrawable(backgroundDrawableRes)
        } else {
            getDrawable(R.drawable.round_shadow_background)
        }
    }

    protected fun getColor(resId: Int): Int {
        return context?.let {
            ContextCompat.getColor(it, resId)
        }.default(0)
    }

    protected fun getDrawable(resId: Int): Drawable? {
        return context?.let {
            ContextCompat.getDrawable(it, resId)
        }
    }

    protected abstract fun onCreationComplete()

    protected open fun provideRootView(viewBinding: ViewBinding?): View? {
        return viewBinding?.root
    }

    protected open fun inflateInject(context: Context?): ViewBinding? {
        return uiBindingInjector.inflateInject(uiBindingProperty, context)
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }

    fun showCentered(parentView: View?) = createPopup(parentView) {
        popupWindow?.showAtLocation(parentView, Gravity.CENTER, 0, 0)
    }
}