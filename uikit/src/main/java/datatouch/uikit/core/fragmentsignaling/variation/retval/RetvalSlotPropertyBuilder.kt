package datatouch.uikit.core.fragmentsignaling.variation.retval

import datatouch.uikit.core.fragmentsignaling.base.RetvalSlotPropertyBuilderBase
import datatouch.uikit.core.fragmentsignaling.base.SigSlotProperty
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun0
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun1
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun2
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotContainer
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction0
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction1
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction2

class RetvalSlotPropertyBuilder<R>(slotContainer: SlotContainer)
    : RetvalSlotPropertyBuilderBase(slotContainer) {

    /**
     *  Create slot for signal without params and with return value
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFun0<R> type
     */
    fun slot(act: ISigSlotAction0<R>): SigSlotProperty<SigFun0<R>> {
        return getSlotContainerOnce().addSlot(act)
    }


    /**
     *  Create slot for signal with 1 param and with return value
     *
     *  A - param type;
     *  R - return value type
     *  @param a must be ignored; it is used for overload only
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFun1<A, R> type
     */
    fun <A : Any?> slot(a: A? = null, act: ISigSlotAction1<A, R>)
    : SigSlotProperty<SigFun1<A, R>> {
        return getSlotContainerOnce().addSlot(act)
    }


    /**
     *  Create slot for signal with 2 params and with return value
     *
     *  A, B - param types;
     *  R - return value type
     *  @param a must be ignored; it is used for overload only
     *  @param b must be ignored; it is used for overload only
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFun2<A, B, R> type
     */
    fun <A : Any?, B : Any?> slot(a: A? = null, b: B? = null, act: ISigSlotAction2<A, B, R>)
    : SigSlotProperty<SigFun2<A, B, R>> {
        return getSlotContainerOnce().addSlot(act)
    }
}