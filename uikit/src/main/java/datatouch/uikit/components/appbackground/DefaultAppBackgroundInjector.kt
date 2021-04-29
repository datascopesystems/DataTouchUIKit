package datatouch.uikit.components.appbackground

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import datatouch.uikit.components.appbackground.interfaces.IAppBackgroundInjector
import datatouch.uikit.core.extensions.ImageViewExtensions.showImageRes


abstract class DefaultAppBackgroundInjector : IAppBackgroundInjector {

    protected val allBackgrounds by lazy { AppBackground.List.createFromAll() }

    protected fun setBackground(view: View, resourceId: Int, defaultColor: Int = Color.DKGRAY) {
        if (view is ImageView) {
            view.showImageRes(resourceId)
            return
        }

        val resources: Resources? = view.resources
        if (resources == null) {
            view.setBackgroundColor(defaultColor)
            return
        }

        try {
            view.setBackgroundResource(resourceId)
        } catch (error: OutOfMemoryError) {
            view.setBackgroundColor(defaultColor)
        }
    }



}