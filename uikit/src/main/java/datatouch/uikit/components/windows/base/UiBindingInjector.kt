package datatouch.uikit.components.windows.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import datatouch.uikit.core.utils.views.ViewBindingUtil
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField


typealias UiBindingProperty = KProperty<ViewBinding?>
typealias UiBindingMutableProperty = KMutableProperty0<ViewBinding?>

class UiBindingInjector {

    private var targetProperty: UiBindingProperty? = null

    private fun setPropertyValueWithNullableCheck(property: UiBindingMutableProperty, value: ViewBinding?) {
        val isAssignAllowed: Boolean = !(value == null && !property.returnType.isMarkedNullable)
        if (isAssignAllowed) {
            property.set(value)
        }
    }

    private fun setPropertyValue(property: UiBindingProperty?, value: ViewBinding?) {
        if (property != null) {
            if (property is UiBindingMutableProperty) {
                setPropertyValueWithNullableCheck(property, value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun inflateViewBindingFromProperty(property: UiBindingProperty?, inflater: LayoutInflater): ViewBinding? {
        return property?.javaField?.type?.let {
            ViewBindingUtil.inflate(inflater, it as Class<ViewBinding>)
        }
    }

    fun inflateInject(targetProperty: UiBindingProperty, context: Context?): ViewBinding? {
        this.targetProperty = targetProperty
        val ui = inflateViewBindingFromProperty(targetProperty, LayoutInflater.from(context))
        setPropertyValue(targetProperty, ui)
        return ui
    }

    @Suppress("UNCHECKED_CAST")
    private fun inflateViewBindingFromProperty(property: UiBindingProperty?, inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean): ViewBinding? {
        return property?.javaField?.type?.let {
            ViewBindingUtil.inflate(inflater, container, attachToRoot, it as Class<ViewBinding>)
        }
    }

    fun inflateInject(targetProperty: UiBindingProperty, inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean): ViewBinding? {
        this.targetProperty = targetProperty
        val ui = inflateViewBindingFromProperty(targetProperty, inflater, container, attachToRoot)
        setPropertyValue(targetProperty, ui)
        return ui
    }

    fun releaseInjected() {
        setPropertyValue(targetProperty, null)
        targetProperty = null
    }
}