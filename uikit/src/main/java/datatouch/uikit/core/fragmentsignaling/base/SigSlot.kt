package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.fragmentsignaling.interfaces.ISigSlotExecutable

internal abstract class SigSlot<Act>(override val slotId: SigSlotId,
                                     protected var action: Act?): ISigSlotExecutable {
    override fun drop() {
        action = null
    }

    override fun isSlotNumberEquals(id: SigSlotId): Boolean {
        return this.slotId.slotNumber == id.slotNumber
    }
}