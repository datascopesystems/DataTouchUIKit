package datatouch.uikit.core.fragmentargs.property

import android.os.Bundle
import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentargs.interfaces.IFragmentArgProperty
import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.cast

internal class ArgPropertyNullable<V : Serializable>(private var internalValue: V?)
    : BaseArgProperty<V?>(), IFragmentArgProperty<V?> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V? {
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

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: V?) {
        assignInternalValue(value)

        val argumentsBundle = getArguments(thisRef)
        if (saveNullArgumentIfRequired(argumentsBundle, property, value)) {
            return
        }

        saveArgument(argumentsBundle, property.name, value)
    }

    private fun saveNullArgumentIfRequired(dst: Bundle, property: KProperty<*>, value: V?): Boolean {
        if (value == null) {
            if (property.returnType.isMarkedNullable) {
                dst.putSerializable(property.name, null)
            }
            return true
        }
        return false
    }

    private fun restoreInternalValue(src: Bundle, property: KProperty<*>) {
        val value = restoreArgument(src, property.name)
        if (value == null) {
            assignInternalValue(null)
            return
        }

        if (castToInternalValueType(value)) {
            return
        }

        if (castToPropertyType(value, property, true)) {
            return
        }
    }

    private fun castToInternalValueType(newValue: Any): Boolean {
        val castedValue = internalValue?.let {
            runCatching { it::class.cast(newValue) }.getOrNull()
        }

        if (castedValue != null) {
            assignInternalValue(castedValue)
            return true
        }

        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun castToType(value: Any): V? {
        return runCatching { value as V? }.getOrNull()
    }

    override fun assignInternalValue(value: V?) {
        internalValue = value
        isInitialized = true
    }
}