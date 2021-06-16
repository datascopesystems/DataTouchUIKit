package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalSharedViewModel
import datatouch.uikit.core.fragmentsignaling.interfaces.ISignal
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotIdOwner

internal abstract class SigCallInvoker<R>(override val slotId: SigSlotId,
                                          private var vm: SignalSharedViewModel?): ISlotIdOwner {

    protected fun emitSignal(signal: ISignal) {
        vm?.emitSignal(signal)
        drop()
    }

    override fun drop() {
        vm = null
    }
}