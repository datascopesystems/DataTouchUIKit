package datatouch.uikit.core.fragmentsignaling.variation.sigfun


interface SigFunVoid0 : ISignalFunction0 {
     operator fun invoke()
     fun invokeBlocking()
}

interface SigFunVoid1<in A> : ISignalFunction1 {
     operator fun invoke(a: A)
     fun invokeBlocking(a: A)
}

interface SigFunVoid2<in A, in B> : ISignalFunction2 {
     operator fun invoke(a: A, b: B)
     fun invokeBlocking(a: A, b: B)
}