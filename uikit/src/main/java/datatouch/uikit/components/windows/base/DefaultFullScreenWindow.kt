package datatouch.uikit.components.windows.base

import android.annotation.SuppressLint
import android.view.View
import datatouch.uikit.R
import datatouch.uikit.components.windows.base.toolbars.DefaultFullScreenWindowToolbar


@SuppressLint("InflateParams")
@Deprecated("Inherit from DefaultFullScreenWindowUiBind instead")
abstract class DefaultFullScreenWindow : FullScreenWindow<DefaultFullScreenWindowToolbar>() {

    override fun provideToolbar() : DefaultFullScreenWindowToolbar {
        val defaultToolBar = DefaultFullScreenWindowToolbar(context)

        defaultToolBar.titleText = if (getWindowTitle() == 0) getCustomTitle()
        else getString(getWindowTitle())

        defaultToolBar.setOnBackButtonListener {
            onClose()
            dismiss()
        }

        return defaultToolBar
    }

    protected open fun getWindowTitle(): Int = R.string.no_title

    protected open fun getCustomTitle() = ""

    protected fun setButtonsPanel(view: View) = windowToolBar?.addViewToButtonsContainer(view)

    protected fun setWindowTitle(title: String?) = windowToolBar?.setTitle(title)

}