package datatouch.uikit.components.appbackground

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import datatouch.uikit.components.appbackground.interfaces.IAppBackgroundInjector

abstract class DefaultAppBackgroundInjector : IAppBackgroundInjector {

    private val allBackgrounds by lazy { AppBackground.List.createFromAll() }

    protected fun setBackground(view: View, resourceId: Int, defaultColor: Int = Color.DKGRAY) {
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