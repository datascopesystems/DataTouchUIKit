package datatouch.uikit.core.fragmentsignaling.variation.invokers


import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentsignaling.base.SigFunInvokerProperty
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*
import kotlin.reflect.KProperty


internal class SigFunInvokerProperty0<R, F : ISignalFunction0> : SigFunInvokerProperty<F>() {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): F {
        val id = getSigSlotId(thisRef, property)
        val invokerName = getSigFunInvokerName(property)
        return SigFunInvoker0<R>(id, invokerName, getSharedViewModel(thisRef)) as F
    }
}

internal class SigFunInvokerProperty1<A, R, F : ISignalFunction1> : SigFunInvokerProperty<F>() {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): F {
        val id = getSigSlotId(thisRef, property)
        val invokerName = getSigFunInvokerName(property)
        return SigFunInvoker1<A, R>(id, invokerName, getSharedViewModel(thisRef)) as F
    }
}

internal class SigFunInvokerProperty2<A, B, R, F : ISignalFunction2> : SigFunInvokerProperty<F>() {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): F {
        val id = getSigSlotId(thisRef, property)
        val invokerName = getSigFunInvokerName(property)
        return SigFunInvoker2<A, B, R>(id, invokerName, getSharedViewModel(thisRef)) as F
    }
}
