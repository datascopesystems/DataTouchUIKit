package datatouch.uikit.core.fragmentsignaling.variation.call

import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotIdOwner
import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotRetValAction

interface SigCall0<R> : ISlotIdOwner {
    operator fun invoke(act: ISlotRetValAction<R>? = null)
}

interface SigCall1<in A,  R> : ISlotIdOwner {
    operator fun invoke(a: A, act: ISlotRetValAction<R>? = null)
}

interface SigCall2<in A, in B, R> : ISlotIdOwner {
    operator fun invoke(a: A, b: B, act: ISlotRetValAction<R>? = null)
}