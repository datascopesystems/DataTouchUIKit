package datatouch.uikit.core.fragmentsignaling.consumer

import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.core.fragmentsignaling.base.SigSlotId
import datatouch.uikit.core.fragmentsignaling.interfaces.IDropableSignal
import datatouch.uikit.core.fragmentsignaling.interfaces.ISigSlotExecutable
import datatouch.uikit.core.fragmentsignaling.interfaces.ISignal


abstract class SlotExecutableContainer : IDropableSignal {

    private var slots: MutableList<ISigSlotExecutable>? = null

    protected var consumerName = ""
        private set

    protected fun setConsumerName(name: String?) {
        consumerName = name.default("")
    }

    private fun addExecutableInternal(executable: ISigSlotExecutable) {
        if (slots == null) {
            slots = mutableListOf()
        }

        slots?.add(executable)
    }

    protected fun <T: ISigSlotExecutable> addExecutable(invokable: T): T {
        addExecutableInternal(invokable)
        return invokable
    }

    protected fun consumeSignal(signal: ISignal) {
        if (signal.isNotBelongsToConsumer(consumerName)) {
            return
        }

        val slot = getSlotForSignal(signal)
        if (slot != null) {
            executeSignalSlot(slot, signal)
        }
    }

    private fun executeSignalSlot(sigSlot: ISigSlotExecutable, signal: ISignal) {
        val retValResult = sigSlot.execute(signal.getSlotParameters())
        if (retValResult.isSuccess()) {
            runCatching { signal.execRetValAction(retValResult.value) }
        }
        retValResult.drop()
    }

    private fun getSlotForSignal(signal: ISignal): ISigSlotExecutable? {
        return slots?.firstOrNull { it.isSlotNumberEquals(signal.slotId) }
    }

    override fun drop() {
        slots?.forEach { it.drop() }
        slots?.clear()
        slots = null
    }

    protected fun createSlotId() = SigSlotId(consumerName, getSize())

    protected fun getSize(): Int = slots?.size.default(0)
}