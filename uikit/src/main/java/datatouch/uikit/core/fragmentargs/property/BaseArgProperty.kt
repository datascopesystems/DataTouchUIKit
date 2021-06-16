package datatouch.uikit.core.fragmentargs.property

import android.os.Bundle
import androidx.fragment.app.Fragment
import datatouch.uikit.core.extensions.GenericExtensions.default
import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType

internal abstract class BaseArgProperty<V> {
    protected var isInitialized = false


    protected abstract fun assignInternalValue(value: V)
    protected abstract fun castToType(value: Any): V?

    protected fun castToPropertyType(newValue: Any, property: KProperty<*>, isValueTypeNullable: Boolean): Boolean {
        val srcType = newValue::class.createType(nullable = isValueTypeNullable)
        val dstType = property.returnType
        if (srcType != dstType) {
            return false
        }

        val castedValue = castToType(newValue)
        if (castedValue != null) {
            assignInternalValue(castedValue)
            return true
        }

        return false
    }

    protected fun saveArgument(dst: Bundle, key: String, value: V) {
        if (value is Serializable) {
            dst.putSerializable(key, value)
        }
    }

    protected fun restoreArgument(src: Bundle, key: String): Any? {
        return src.get(key)
    }

    protected fun isNotExistArgument(src: Bundle, key: String): Boolean {
        return !src.containsKey(key)
    }

    protected fun getArguments(f: Fragment): Bundle {
        return when (val args = f.arguments) {
            null -> Bundle().also { f.arguments = it }
            else -> args
        }
    }

    protected fun isEmptyArguments(f: Fragment): Boolean {
        return f.arguments?.isEmpty.default(true)
    }
}