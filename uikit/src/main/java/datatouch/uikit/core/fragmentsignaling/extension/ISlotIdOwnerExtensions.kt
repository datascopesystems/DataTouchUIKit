package datatouch.uikit.core.fragmentsignaling.extension

import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotIdOwner
import kotlin.reflect.KProperty

internal fun ISlotIdOwner.assignSlotNameFromProperty(srcProperty: KProperty<*>) {
    if (slotId.hasSlotName()) {
        return
    }

    slotId.assignSlotName(srcProperty.name)
}