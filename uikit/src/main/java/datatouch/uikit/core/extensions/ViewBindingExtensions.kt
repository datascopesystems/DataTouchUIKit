package datatouch.uikit.core.extensions

import androidx.viewbinding.ViewBinding
import datatouch.uikit.core.extensions.ConditionsExtensions.isNull
import java.lang.reflect.ParameterizedType

object ViewBindingExtensions {

    @Suppress("UNCHECKED_CAST")
    fun <TLayout : ViewBinding> getViewBindingClass(viewClass: Class<Any>): Class<TLayout> =
        getBindingClassName(viewClass)

    private fun <TLayout : ViewBinding> getBindingClassName(viewClass: Class<Any>): Class<TLayout> {
        if (viewClass.isNull()) throw IllegalArgumentException("Binding class name was not found in class generic hierarchy")

        var layoutClass: Class<TLayout>? = null
        kotlin.runCatching {
            @Suppress("UNCHECKED_CAST")
            layoutClass =
                (viewClass.genericSuperclass as? ParameterizedType?)?.actualTypeArguments?.firstOrNull() as? Class<TLayout>?
        }

        if (layoutClass == null)
            return getBindingClassName(viewClass.superclass)

        return layoutClass!!
    }

}