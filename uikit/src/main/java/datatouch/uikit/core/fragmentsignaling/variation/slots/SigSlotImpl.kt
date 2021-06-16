package datatouch.uikit.core.fragmentsignaling.variation.slots


import datatouch.uikit.core.fragmentsignaling.base.SigSlot
import datatouch.uikit.core.fragmentsignaling.base.SigSlotId
import datatouch.uikit.core.fragmentsignaling.base.SlotExecResult
import datatouch.uikit.core.fragmentsignaling.exceptions.SlotExecArgsException
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall1
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotRetValAction


internal class SigSlot0<R>(slotId: SigSlotId, act: ISigSlotAction0<R>)
    : SigSlot<ISigSlotAction0<R>>(slotId, act) , SigCall0<R> {

    override fun execute(args: Array<Any?>?): SlotExecResult {
        return runCatching { action?.invoke() }
            .map { SlotExecResult.success(it) }
            .getOrElse { SlotExecResult.error(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(act: ISlotRetValAction<R>?) {
        val result = action?.invoke()
        runCatching { act?.invoke(result as R) }
    }
}


internal class SigSlot1<A, R>(slotId: SigSlotId, act: ISigSlotAction1<A, R>,
                              private val isNullableA: Boolean)
    : SigSlot<ISigSlotAction1<A, R>>(slotId, act) , SigCall1<A, R> {

    @Suppress("UNCHECKED_CAST")
    override fun execute(args: Array<Any?>?): SlotExecResult {
        if (args?.size != 1) {
            return SlotExecResult.error(SlotExecArgsException())
        }

        return runCatching {
            val a = checkIsNullParam(args.component1(), isNullableA)
            action?.invoke(a as A)
        }.map {
            SlotExecResult.success(it)
        }.getOrElse { SlotExecResult.error(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(a: A, act: ISlotRetValAction<R>?) {
        val result = action?.invoke(a)
        runCatching { act?.invoke(result as R) }
    }
}


internal class SigSlot2<A, B, R>(slotId: SigSlotId, act: ISigSlotAction2<A, B, R>,
                                 private val isNullableA: Boolean,
                                 private val isNullableB: Boolean)
    : SigSlot<ISigSlotAction2<A, B, R>>(slotId, act) , SigCall2<A, B, R> {

    @Suppress("UNCHECKED_CAST")
    override fun execute(args: Array<Any?>?): SlotExecResult {
        if (args?.size != 2) {
            return SlotExecResult.error(SlotExecArgsException())
        }

        return runCatching {
            val a = checkIsNullParam(args.component1(), isNullableA)
            val b = checkIsNullParam(args.component2(), isNullableB)
            action?.invoke(a as A, b as B)
        }.map {
            SlotExecResult.success(it)
        }.getOrElse { SlotExecResult.error(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(a: A, b: B, act: ISlotRetValAction<R>?) {
        val result = action?.invoke(a, b)
        runCatching { act?.invoke(result as R) }
    }
}