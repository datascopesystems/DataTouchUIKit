package datatouch.uikit.core.fragmentsignaling.variation.invokers


import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentsignaling.base.SigCallInvokerProperty
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall1
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import kotlin.reflect.KProperty


internal class SigCallInvokerProperty0<R> : SigCallInvokerProperty<SigCall0<R>>() {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): SigCall0<R> {
        val id = getSigSlotId(thisRef, property)
        return SigCallInvoker0(id, getSharedViewModel(thisRef))
    }
}

internal class SigCallInvokerProperty1<A, R> : SigCallInvokerProperty<SigCall1<A, R>>() {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): SigCall1<A, R> {
        val id = getSigSlotId(thisRef, property)
        return SigCallInvoker1(id, getSharedViewModel(thisRef))
    }
}

internal class SigCallInvokerProperty2<A, B, R> : SigCallInvokerProperty<SigCall2<A, B, R>>() {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): SigCall2<A, B, R> {
        val id = getSigSlotId(thisRef, property)
        return SigCallInvoker2(id, getSharedViewModel(thisRef))
    }
}
