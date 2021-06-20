package datatouch.uikit.core.fragmentsignaling.variation.retval

import datatouch.uikit.core.fragmentsignaling.interfaces.ISigFunProperty
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty0
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty1
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty2
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun0
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun1
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun2

class RetvalSigFunPropertyBuilder<R> {

    /**
     *  Delegate for SigFun0<R>
     *  signal without params but with return value
     *  @return readonly property delegate for SigFun0 type
     */
    fun sigFun(): ISigFunProperty<SigFun0<R>> {
        return SigFunInvokerProperty0<R, SigFun0<R>>()
    }

    /**
     *  Delegate for SigFun1<A, R>
     *  signal with 1 param and with return value
     *
     *  A - param type;
     *  R - return value type
     *  @param a must be ignored; it is used for overload only
     *  @return readonly property delegate for SigFun1<A, R> type
     */
    fun <A> sigFun(a: A? = null): ISigFunProperty<SigFun1<A, R>> {
        return SigFunInvokerProperty1<A, R, SigFun1<A, R>>()
    }

    /**
     *  Delegate for SigFun1<A, B, R>
     *  signal with 2 params and with return value
     *
     *  A, B - param types;
     *  R - return value type
     *  @param a must be ignored; it is used for overload only
     *  @return readonly property delegate for SigFun1<A, B, R> type
     */
    fun <A, B> sigFun(a: A? = null, b: B? = null): ISigFunProperty<SigFun2<A, B, R>> {
        return SigFunInvokerProperty2<A, B, R, SigFun2<A, B, R>>()
    }
}