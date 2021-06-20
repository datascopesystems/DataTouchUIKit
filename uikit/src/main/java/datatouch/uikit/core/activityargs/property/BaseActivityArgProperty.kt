package datatouch.uikit.core.activityargs.property

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.core.activityargs.extension.makeIntentKeyName
import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType


internal abstract class BaseActivityArgProperty<V> {
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

    protected fun saveNullArgumentIfRequired(dst: Intent?, property: KProperty<*>, value: V?): Boolean {
        if (value == null) {
            if (property.returnType.isMarkedNullable) {
                val nullValue: Serializable? = null
                dst?.putExtra(makeIntentKeyName(property.name), nullValue)
            }
            return true
        }
        return false
    }

    protected fun saveArgument(dst: Intent?, key: String, value: V) {
        if (value is Serializable) {
            dst?.putExtra(makeIntentKeyName(key), value)
        }
    }

    protected fun restoreArgument(src: Intent, key: String): Any? {
        return src.getSerializableExtra(makeIntentKeyName(key))
    }

    protected fun isNotExistArgument(src: Intent, key: String): Boolean {
        return !src.hasExtra(makeIntentKeyName(key))
    }

    protected fun getIntent(a: AppCompatActivity): Intent? {
        return a.intent
    }
}