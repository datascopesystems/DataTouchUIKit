package datatouch.uikit.core.fragmentsignaling.variation.invokers

import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalSharedViewModel
import datatouch.uikit.core.fragmentsignaling.base.SigSlotId
import datatouch.uikit.core.fragmentsignaling.base.SigFunInvoker
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotRetValAction
import datatouch.uikit.core.fragmentsignaling.base.Signal
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*


internal class SigFunInvoker0<R>(slotId: SigSlotId, invokerName: String?, vm: SignalSharedViewModel?)
    : SigFunInvoker(slotId, invokerName, vm), SigFun0<R>, SigFunVoid0 {

    override fun invoke(act: ISlotRetValAction<R>) {
        emitSignal(Signal(slotId, invokerName, act))
    }

    override fun invoke() {
        emitSignal(Signal<R>(slotId, invokerName, null))
    }

    override fun invokeBlocking(act: ISlotRetValAction<R>) {
        emitSignalBlocking(Signal(slotId, invokerName, act))
    }

    override fun invokeBlocking() {
        emitSignalBlocking(Signal<R>(slotId, invokerName, null))
    }
}

internal class SigFunInvoker1<in A, R>(slotId: SigSlotId, invokerName: String?, vm: SignalSharedViewModel?)
    : SigFunInvoker(slotId, invokerName, vm), SigFun1<A, R>, SigFunVoid1<A> {

    override fun invoke(a: A, act: ISlotRetValAction<R>) {
        emitSignal(Signal(slotId, invokerName, act, arrayOf(a)))
    }

    override fun invoke(a: A) {
        emitSignal(Signal<R>(slotId, invokerName, null, arrayOf(a)))
    }

    override fun invokeBlocking(a: A, act: ISlotRetValAction<R>) {
        emitSignalBlocking(Signal(slotId, invokerName, act, arrayOf(a)))
    }

    override fun invokeBlocking(a: A) {
        emitSignalBlocking(Signal<R>(slotId, invokerName, null, arrayOf(a)))
    }
}

internal class SigFunInvoker2<in A, in B, R>(slotId: SigSlotId, invokerName: String?, vm: SignalSharedViewModel?)
    : SigFunInvoker(slotId, invokerName, vm), SigFun2<A, B, R>, SigFunVoid2<A, B> {

    override fun invoke(a: A, b: B, act: ISlotRetValAction<R>) {
        emitSignal(Signal(slotId, invokerName, act, arrayOf(a, b)))
    }

    override fun invoke(a: A, b: B) {
        emitSignal(Signal<R>(slotId, invokerName, null, arrayOf(a, b)))
    }

    override fun invokeBlocking(a: A, b: B, act: ISlotRetValAction<R>) {
        emitSignalBlocking(Signal(slotId, invokerName, act, arrayOf(a, b)))
    }

    override fun invokeBlocking(a: A, b: B) {
        emitSignalBlocking(Signal<R>(slotId, invokerName, null, arrayOf(a, b)))
    }
}
