package datatouch.uikit.core.fragmentsignaling.variation.builders

import datatouch.uikit.core.fragmentsignaling.base.BuilderSlotProperty
import datatouch.uikit.core.fragmentsignaling.base.SigSlotProperty
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotContainer
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction0
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction1
import datatouch.uikit.core.fragmentsignaling.variation.slots.ISigSlotAction2


class BuilderSlot0(slotContainer: SlotContainer) : BuilderSlotProperty(slotContainer) {

    /**
     *  Create slot for signal with no params and no return value
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFunVoid0 type
     */
    fun of(act: ISigSlotAction0<Unit>): SigSlotProperty<SigFunVoid0> {
        return getSlotContainerOnce().addSlot(act)
    }

    /**
     *  Create slot for signal with no params and with return value
     *
     *  R - return value type
     *  @param ignore must be ignored; it is used for overload only
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFun0<R> type
     */
    fun <R> of(ignore: R? = null, act: ISigSlotAction0<R>): SigSlotProperty<SigFun0<R>> {
        return getSlotContainerOnce().addSlot(act)
    }
}

class BuilderSlot1<A>(slotContainer: SlotContainer) : BuilderSlotProperty(slotContainer) {

    /**
     *  Create slot for signal with 1 param and no return value
     *
     *  A - param type;
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFunVoid1 type
     */
    fun of(act: ISigSlotAction1<A, Unit>): SigSlotProperty<SigFunVoid1<A>> {
        return getSlotContainerOnce().addSlot(act)
    }

    /**
     *  Create slot for signal with 1 param and with return value
     *
     *  A - param type;
     *  R - return value type
     *  @param ignore must be ignored; it is used for overload only
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFun1<A, R> type
     */
    fun <R> of(ignore: R? = null, act: ISigSlotAction1<A, R>): SigSlotProperty<SigFun1<A, R>> {
        return getSlotContainerOnce().addSlot(act)
    }
}

class BuilderSlot2<A, B>(slotContainer: SlotContainer) : BuilderSlotProperty(slotContainer) {

    /**
     *  Create slot for signal with 2 params and no return value
     *
     *  A, B - param types;
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFunVoid2 type
     */
    fun of(act: ISigSlotAction2<A, B, Unit>): SigSlotProperty<SigFunVoid2<A, B>> {
        return getSlotContainerOnce().addSlot(act)
    }


    /**
     *  Create slot for signal with 2 params and with return value
     *
     *  A, B - param types;
     *  R - return value type
     *  @param ignore must be ignored; it is used for overload only
     *  @param act signal handler - lambda or function reference
     *  @return readonly property delegate for SigFun2<A, B, R> type
     */
    fun <R> of(ignore: R? = null, act: ISigSlotAction2<A, B, R>): SigSlotProperty<SigFun2<A, B, R>> {
        return getSlotContainerOnce().addSlot(act)
    }
}