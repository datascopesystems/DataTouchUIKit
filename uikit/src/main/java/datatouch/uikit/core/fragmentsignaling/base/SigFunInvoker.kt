package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalSharedViewModel
import datatouch.uikit.core.fragmentsignaling.interfaces.ISignal
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotIdOwner

internal abstract class SigFunInvoker(override val slotId: SigSlotId,
                                      protected var invokerName: String?,
                                      private var vm: SignalSharedViewModel?): ISlotIdOwner {

    protected fun emitSignal(signal: ISignal) {
        vm?.emitSignal(signal)
        drop()
    }

    override fun drop() {
        vm = null
        invokerName = null
    }
}