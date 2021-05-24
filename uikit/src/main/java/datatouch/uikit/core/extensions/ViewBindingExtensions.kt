package datatouch.uikit.core.extensions

import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

object ViewBindingExtensions {

    @Suppress("UNCHECKED_CAST")
    fun <TLayout : ViewBinding> getViewBindingClass(viewClass: Class<Any>): Class<TLayout> {
        return if (viewClass.name.endsWith("_")) { // Workaround for Android Annotations
            val superClass = viewClass.superclass
            val genericSuperclass = superClass.genericSuperclass
            (genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<TLayout>
        } else {
            (viewClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<TLayout>
        }
    }

}