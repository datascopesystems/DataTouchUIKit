package datatouch.uikit.core.fragmentsignaling.variation.sigfun

import datatouch.uikit.core.fragmentsignaling.interfaces.ISlotRetValAction


interface SigFun0<R> : ISignalFunction0 {
     operator fun invoke(act: ISlotRetValAction<R>)
}

interface SigFun1<in A, R> : ISignalFunction1 {
     operator fun invoke(a: A, act: ISlotRetValAction<R>)
}

interface SigFun2<in A, in B, R> : ISignalFunction2 {
     operator fun invoke(a: A, b: B, act: ISlotRetValAction<R>)
}