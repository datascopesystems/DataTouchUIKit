package datatouch.uikit.core.fragmentsignaling.variation.slotcontainer

import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall1
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction0
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction1
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction2

abstract class SlotCreationContainer : SlotContainer() {

    fun <R> slot(act: ISigSlotAction0<R>): SigCall0<R> {
        return addSlot(act)
    }

    inline fun <reified A : Any?, R> slot(a: A? = null,
                                          act: ISigSlotAction1<A, R>)
    : SigCall1<A, R> {
        return this.addSlot(act, null is A)
    }

    inline fun <reified A : Any?, reified B : Any?, R> slot(a: A? = null,
                                                            b: B? = null,
                                                            act: ISigSlotAction2<A, B, R>)
    : SigCall2<A, B, R> {
        return this.addSlot(act, null is A, null is B)
    }
}