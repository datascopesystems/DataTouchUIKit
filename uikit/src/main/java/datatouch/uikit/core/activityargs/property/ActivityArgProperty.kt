package datatouch.uikit.core.activityargs.property

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.core.activityargs.interfaces.IActivityArgProperty
import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.cast

internal class ActivityArgProperty<V : Serializable>(private var internalValue: V)
    : BaseActivityArgProperty<V>(), IActivityArgProperty<V> {

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): V {
        if (isInitialized) {
            return internalValue
        }

        val intent = getIntent(thisRef) ?: return internalValue

        if (isNotExistArgument(intent, property.name)) {
            return internalValue
        }

        restoreInternalValue(intent, property)

        return internalValue
    }

    override fun setValue(thisRef: AppCompatActivity, property: KProperty<*>, value: V) {
        assignInternalValue(value)
        saveArgument(getIntent(thisRef), property.name, value)
    }

    private fun restoreInternalValue(src: Intent, property: KProperty<*>) {
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