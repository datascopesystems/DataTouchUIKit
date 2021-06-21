package datatouch.uikit.core.fragmentsignaling.variation.builders

import datatouch.uikit.core.fragmentsignaling.interfaces.ISigFunProperty
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty0
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty1
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty2
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*

class BuilderSigFun0 {

    /**
     *  Create delegate for SigFunVoid0
     *  signal with no params and no return value
     *  @return property delegate for SigFunVoid0 type
     */
    fun of(): ISigFunProperty<SigFunVoid0> {
        return SigFunInvokerProperty0<Unit, SigFunVoid0>()
    }

    /**
     *  Create Delegate for SigFun0<R>
     *  signal without params but with return value
     *  @return readonly property delegate for SigFun0 type
     */
    fun <R> of(ignore: R? = null): ISigFunProperty<SigFun0<R>> {
        return SigFunInvokerProperty0<R, SigFun0<R>>()
    }
}

class BuilderSigFun1<A> {

    /**
     *  Create delegate for SigFunVoid1
     *  signal with 1 param and no return value
     *
     *  A - param type
     *  @return property delegate for SigFunVoid1 type
     */
    fun of(): ISigFunProperty<SigFunVoid1<A>> {
        return SigFunInvokerProperty1<A, Unit, SigFunVoid1<A>>()
    }

    /**
     *  Create delegate for SigFun1<A, R>
     *  signal with 1 param and with return value
     *
     *  A - param type;
     *  R - return value type
     *  @param ignore must be ignored; it is used for overload only
     *  @return readonly property delegate for SigFun1<A, R> type
     */
    fun <R> of(ignore: R? = null): ISigFunProperty<SigFun1<A, R>> {
        return SigFunInvokerProperty1<A, R, SigFun1<A, R>>()
    }
}

class BuilderSigFun2<A, B> {

    /**
     *  Create Delegate for SigFunVoid2
     *  signal with 2 params param and no return value
     *
     *  A, B - param types
     *  @return property delegate for SigFunVoid2 type
     */
    fun of(): ISigFunProperty<SigFunVoid2<A, B>> {
        return SigFunInvokerProperty2<A, B, Unit, SigFunVoid2<A, B>>()
    }

    /**
     *  Create delegate for SigFun1<A, B, R>
     *  signal with 2 params and with return value
     *
     *  A, B - param types;
     *  R - return value type
     *  @param ignore must be ignored; it is used for overload only
     *  @return readonly property delegate for SigFun1<A, B, R> type
     */
    fun <R> of(ignore: R? = null): ISigFunProperty<SigFun2<A, B, R>> {
        return SigFunInvokerProperty2<A, B, R, SigFun2<A, B, R>>()
    }
}