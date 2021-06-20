package datatouch.uikit.core.fragmentsignaling.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalSharedViewModel
import datatouch.uikit.core.fragmentsignaling.interfaces.ISigFunProperty
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotIdOwner
import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalViewModelProvider
import kotlin.reflect.KProperty
import kotlin.reflect.cast

internal abstract class SigFunInvokerProperty<V : ISlotIdOwner> : ISigFunProperty<V> {
    private var sigSlotId = SigSlotId.createEmpty()

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: V) {
        setSigSlotId(thisRef, property, value.slotId)
    }

    protected fun getSharedViewModel(fragment: Fragment): SignalSharedViewModel {
        return SignalViewModelProvider.of(fragment)
    }

    private fun setSigSlotId(thisRef: Fragment, property: KProperty<*>, sigSlotId: SigSlotId) {
        this.sigSlotId = sigSlotId
        saveArgument(getArguments(thisRef), property.name, sigSlotId)
    }

    protected fun getSigSlotId(thisRef: Fragment, property: KProperty<*>): SigSlotId {
        if (sigSlotId.isNotEmpty() || isEmptyArguments(thisRef)) {
            return sigSlotId
        }

        val argumentsBundle = getArguments(thisRef)
        if (isNotExistArgument(argumentsBundle, property.name)) {
            return sigSlotId
        }

        restoreSignalHandlerId(argumentsBundle, property)

        return sigSlotId
    }

    protected fun getSigFunInvokerName(property: KProperty<*>): String {
        return property.name
    }

    protected fun restoreSignalHandlerId(src: Bundle, property: KProperty<*>) {
        val value = restoreArgument(src, property.name) ?: return

        if (castToSignalHandlerId(value)) {
            return
        }
    }

    private fun castToSignalHandlerId(newValue: Any): Boolean {
        val castedValue = runCatching {
            sigSlotId::class.cast(newValue)
        }.getOrNull()

        if (castedValue != null) {
            sigSlotId = castedValue
            return true
        }

        return false
    }

    protected fun saveArgument(dst: Bundle, key: String, value: SigSlotId) {
        if (value.isNotEmpty()) {
            dst.putSerializable(key, value)
        }
    }

    protected fun restoreArgument(src: Bundle, key: String): Any? {
        return src.get(key)
    }

    private fun isNotExistArgument(src: Bundle, key: String): Boolean {
        return !src.containsKey(key)
    }

    private fun getArguments(f: Fragment): Bundle {
        return when (val args = f.arguments) {
            null -> Bundle().also { f.arguments = it }
            else -> args
        }
    }

    private fun isEmptyArguments(f: Fragment): Boolean {
        return f.arguments?.isEmpty.default(true)
    }
}