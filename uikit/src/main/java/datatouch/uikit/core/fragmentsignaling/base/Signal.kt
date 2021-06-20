package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.core.fragmentsignaling.interfaces.ISignal
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotRetValAction


internal class Signal<R>(override val slotId: SigSlotId,
                         private var invokerName: String?,
                         private var retValAction: ISlotRetValAction<R>? = null,
                         private var slotParameters: Array<Any?>? = null) : ISignal {


    override fun getSlotParameters(): Array<Any?>? {
        return slotParameters
    }

    override fun isNotBelongsToConsumer(consumerName: String): Boolean {
        return slotId.isNotBelongsToConsumer(consumerName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun execRetValAction(returnValue: Any?) {
        retValAction?.invoke(returnValue as R)
        drop()
    }

    override fun getInvokerName(): String {
        return invokerName.default("")
    }

    override fun drop() {
        slotParameters?.fill(null)
        slotParameters = null
        retValAction = null
        invokerName = null
    }
}
