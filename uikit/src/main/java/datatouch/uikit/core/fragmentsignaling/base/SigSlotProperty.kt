package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.fragmentsignaling.extension.assignSlotNameFromProperty
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotIdOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class SigSlotProperty<out V : ISlotIdOwner> internal constructor(private val slotIdOwner: V)
    : ReadOnlyProperty<Any?, V> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        slotIdOwner.assignSlotNameFromProperty(property)
        return slotIdOwner
    }
}