package datatouch.uikit.core.fragmentsignaling.variation.slotcontainer


import datatouch.uikit.core.fragmentsignaling.variation.builders.BuilderSlot0
import datatouch.uikit.core.fragmentsignaling.variation.builders.BuilderSlot1
import datatouch.uikit.core.fragmentsignaling.variation.builders.BuilderSlot2


abstract class SlotCreationContainer : SlotContainer() {

    /**
     *  Create slot builder for signal with no params
     */
    fun slot(): BuilderSlot0 {
        return BuilderSlot0(this)
    }

    /**
     *  Create slot builder for signal with 1 param
     *
     *  A - param type;
     *  @param a - must be ignored; it is used for overload only
     */
    fun <A : Any?> slot(a: A? = null): BuilderSlot1<A> {
        return BuilderSlot1(this)
    }

    /**
     *  Create slot builder for signal with 2 params
     *
     *  A, B - param types;
     *  @param a - must be ignored; it is used for overload only
     *  @param b - must be ignored; it is used for overload only
     */
    fun <A : Any?, B : Any?> slot(a: A? = null, b: B? = null): BuilderSlot2<A, B> {
        return BuilderSlot2(this)
    }
}


