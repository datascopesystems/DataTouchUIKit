package datatouch.uikit.components.windows.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import datatouch.uikit.core.extensions.ViewBindingExtensions
import datatouch.uikit.core.utils.views.ViewBindingUtil

abstract class AppFragmentUiBind<TFragmentLayout : ViewBinding> : AppFragment() {

    protected var ui: TFragmentLayout? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ui = ViewBindingUtil.inflate(
            inflater, container, false,
            ViewBindingExtensions.getViewBindingClass(javaClass)
        )
        return ui?.root
    }

    override fun onDestroyView() {
        ui = null
        super.onDestroyView()
    }

}