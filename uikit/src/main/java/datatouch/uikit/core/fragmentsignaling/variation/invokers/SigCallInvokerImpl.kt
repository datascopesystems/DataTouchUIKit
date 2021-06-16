package datatouch.uikit.core.fragmentsignaling.variation.invokers

import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalSharedViewModel
import datatouch.uikit.core.fragmentsignaling.base.SigSlotId
import datatouch.uikit.core.fragmentsignaling.base.SigCallInvoker
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall1
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotRetValAction
import datatouch.uikit.core.fragmentsignaling.base.Signal


internal class SigCallInvoker0<R>(slotId: SigSlotId, vm: SignalSharedViewModel?)
    : SigCallInvoker<R>(slotId, vm), SigCall0<R> {

    override fun invoke(act: ISlotRetValAction<R>?) {
        emitSignal(Signal(slotId, act))
    }
}

internal class SigCallInvoker1<in A, R>(slotId: SigSlotId, vm: SignalSharedViewModel?)
    : SigCallInvoker<R>(slotId, vm), SigCall1<A, R> {

    override fun invoke(a: A, act: ISlotRetValAction<R>?) {
        emitSignal(Signal(slotId, act, arrayOf(a)))
    }
}

internal class SigCallInvoker2<in A, in B, R>(slotId: SigSlotId, vm: SignalSharedViewModel?)
    : SigCallInvoker<R>(slotId, vm), SigCall2<A, B, R> {

    override fun invoke(a: A, b: B, act: ISlotRetValAction<R>?) {
        emitSignal(Signal(slotId, act, arrayOf(a, b)))
    }
}
