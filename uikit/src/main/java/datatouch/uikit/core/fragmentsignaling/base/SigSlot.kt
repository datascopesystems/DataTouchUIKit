package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.fragmentsignaling.interfaces.ISigSlotExecutable
import datatouch.uikit.core.fragmentsignaling.exceptions.NullableSignalParamsException

internal abstract class SigSlot<Act>(override val slotId: SigSlotId,
                                     protected var action: Act?): ISigSlotExecutable {
    override fun drop() {
        action = null
    }

    override fun isSlotNumberEquals(id: SigSlotId): Boolean {
        return this.slotId.slotNumber == id.slotNumber
    }

    protected fun checkIsNullParam(param: Any?, isNullableDst: Boolean): Any? {
        if (param == null) {
            if (!isNullableDst) {
                throw NullableSignalParamsException()
            }
        }
        return param
    }
}