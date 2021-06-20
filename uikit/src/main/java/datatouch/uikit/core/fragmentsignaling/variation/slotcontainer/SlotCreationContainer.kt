package datatouch.uikit.core.fragmentsignaling.variation.slotcontainer

import datatouch.uikit.core.fragmentsignaling.base.SigSlotProperty
import datatouch.uikit.core.fragmentsignaling.variation.retval.RetvalSlotPropertyBuilder
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction0
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction1
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction2

abstract class SlotCreationContainer : SlotContainer() {

    /**
     *  Create slot for signal without params and no return value
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFunVoid0 type
     */
    fun slot(act: ISigSlotAction0<Unit>): SigSlotProperty<SigFunVoid0> {
        return addSlot(act)
    }


    /**
     *  Create slot for signal with 1 param and no return value
     *
     *  A - param type;
     *  @param a - must be ignored; it is used for overload only
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFunVoid1 type
     */
    fun <A : Any?> slot(a: A? = null, act: ISigSlotAction1<A, Unit>)
    : SigSlotProperty<SigFunVoid1<A>> {
        return addSlot(act)
    }


    /**
     *  Create slot for signal with 2 params and no return value
     *
     *  A, B - param types;
     *  @param a - must be ignored; it is used for overload only
     *  @param b - must be ignored; it is used for overload only
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFunVoid2 type
     */
    fun <A : Any?, B : Any?> slot(a: A? = null, b: B? = null, act: ISigSlotAction2<A, B, Unit>)
    : SigSlotProperty<SigFunVoid2<A, B>> {
        return addSlot(act)
    }


    /**
     *  Specify return value for slots
     *  @return property builder which can create delegates for slots with return value
     */
    fun <R> retVal() = RetvalSlotPropertyBuilder<R>(this)
}


