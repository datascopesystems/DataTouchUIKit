package datatouch.uikit.core.fragmentsignaling.variation.slots


import datatouch.uikit.core.fragmentsignaling.base.SigSlot
import datatouch.uikit.core.fragmentsignaling.base.SigSlotId
import datatouch.uikit.core.fragmentsignaling.base.SlotExecResult
import datatouch.uikit.core.fragmentsignaling.exceptions.SlotExecArgsException
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotRetValAction
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*


internal class SigSlot0<R>(slotId: SigSlotId, act: ISigSlotAction0<R>)
    : SigSlot<ISigSlotAction0<R>>(slotId, act), SigFun0<R>, SigFunVoid0 {

    override fun execute(args: Array<Any?>?): SlotExecResult {
        return runCatching { action?.invoke() }
            .map { SlotExecResult.success(it) }
            .getOrElse { SlotExecResult.error(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(act: ISlotRetValAction<R>) {
        val result = action?.invoke()
        runCatching { act.invoke(result as R) }
    }

    override fun invoke() {
        action?.invoke()
    }
}


internal class SigSlot1<A, R>(slotId: SigSlotId, act: ISigSlotAction1<A, R>)
    : SigSlot<ISigSlotAction1<A, R>>(slotId, act), SigFun1<A, R>, SigFunVoid1<A> {

    @Suppress("UNCHECKED_CAST")
    override fun execute(args: Array<Any?>?): SlotExecResult {
        if (args?.size != 1) {
            return SlotExecResult.error(SlotExecArgsException())
        }

        return runCatching {
            val a = args.component1()
            action?.invoke(a as A)
        }.map {
            SlotExecResult.success(it)
        }.getOrElse { SlotExecResult.error(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(a: A, act: ISlotRetValAction<R>) {
        val result = action?.invoke(a)
        runCatching { act.invoke(result as R) }
    }

    override fun invoke(a: A) {
        action?.invoke(a)
    }
}


internal class SigSlot2<A, B, R>(slotId: SigSlotId, act: ISigSlotAction2<A, B, R>)
    : SigSlot<ISigSlotAction2<A, B, R>>(slotId, act), SigFun2<A, B, R>, SigFunVoid2<A, B> {

    @Suppress("UNCHECKED_CAST")
    override fun execute(args: Array<Any?>?): SlotExecResult {
        if (args?.size != 2) {
            return SlotExecResult.error(SlotExecArgsException())
        }

        return runCatching {
            val a = args.component1()
            val b = args.component2()
            action?.invoke(a as A, b as B)
        }.map {
            SlotExecResult.success(it)
        }.getOrElse { SlotExecResult.error(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(a: A, b: B, act: ISlotRetValAction<R>) {
        val result = action?.invoke(a, b)
        runCatching { act.invoke(result as R) }
    }

    override fun invoke(a: A, b: B) {
        action?.invoke(a, b)
    }
}