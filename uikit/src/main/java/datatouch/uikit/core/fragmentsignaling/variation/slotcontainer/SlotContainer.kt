package datatouch.uikit.core.fragmentsignaling.variation.slotcontainer

import datatouch.uikit.core.fragmentsignaling.consumer.SlotExecutableContainer
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall1
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import datatouch.uikit.core.fragmentsignaling.variation.slots.*
import datatouch.uikit.core.fragmentsignaling.variation.slots.SigSlot0
import datatouch.uikit.core.fragmentsignaling.variation.slots.SigSlot1


abstract class SlotContainer : SlotExecutableContainer() {

    fun <R> addSlot(action: ISigSlotAction0<R>): SigCall0<R> {
        val s = SigSlot0(createSlotId(), action)
        return addExecutable(s)
    }

    fun <A, R> addSlot(action: ISigSlotAction1<A, R>, isNullableA: Boolean): SigCall1<A, R> {
        val s = SigSlot1(createSlotId(), action, isNullableA)
        return addExecutable(s)
    }

    fun <A, B, R> addSlot(action: ISigSlotAction2<A, B, R>,
                          isNullableA: Boolean,
                          isNullableB: Boolean): SigCall2<A, B, R> {

        val s = SigSlot2(createSlotId(), action, isNullableA, isNullableB)
        return addExecutable(s)
    }
}