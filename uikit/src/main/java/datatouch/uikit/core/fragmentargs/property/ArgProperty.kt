package datatouch.uikit.core.fragmentargs.property

import android.os.Bundle
import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentargs.interfaces.IFragmentArgProperty
import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.cast


internal class ArgProperty<V : Serializable>(private var internalValue: V)
    : BaseArgProperty<V>(), IFragmentArgProperty<V> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        if (isInitialized || isEmptyArguments(thisRef)) {
            return internalValue
        }

        val argumentsBundle = getArguments(thisRef)
        if (isNotExistArgument(argumentsBundle, property.name)) {
            return internalValue
        }

        restoreInternalValue(argumentsBundle, property)

        return internalValue
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: V) {
        assignInternalValue(value)
        saveArgument(getArguments(thisRef), property.name, value)
    }

    private fun restoreInternalValue(src: Bundle, property: KProperty<*>) {
        val value = restoreArgument(src, property.name) ?: return

        if (castToInternalValueType(value)) {
            return
        }

        if (castToPropertyType(value, property, false)) {
            return
        }
    }

    private fun castToInternalValueType(newValue: Any): Boolean {
        val castedValue = runCatching {
            internalValue::class.cast(newValue)
        }.getOrNull()

        if (castedValue != null) {
            assignInternalValue(castedValue)
            return true
        }

        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun castToType(value: Any): V? {
        return runCatching { value as V }.getOrNull()
    }

    override fun assignInternalValue(value: V) {
        internalValue = value
        isInitialized = true
    }
}