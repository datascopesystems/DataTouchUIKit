package datatouch.uikit.core.activityargs.property

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.core.activityargs.interfaces.IActivityArgProperty
import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.cast

internal class ActivityArgPropertyNullable<V : Serializable>(private var internalValue: V?)
    : BaseActivityArgProperty<V?>(), IActivityArgProperty<V?> {

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): V? {
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

    override fun setValue(thisRef: AppCompatActivity, property: KProperty<*>, value: V?) {
        assignInternalValue(value)

        val intent = getIntent(thisRef)
        if (saveNullArgumentIfRequired(intent, property, value)) {
            return
        }

        saveArgument(intent, property.name, value)
    }

    private fun restoreInternalValue(src: Intent, property: KProperty<*>) {
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