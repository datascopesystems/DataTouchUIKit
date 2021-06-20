package datatouch.uikit.core.fragmentsignaling.variation.sigfun


interface SigFunVoid0 : ISignalFunction0 {
     operator fun invoke()
}

interface SigFunVoid1<in A> : ISignalFunction1 {
     operator fun invoke(a: A)
}

interface SigFunVoid2<in A, in B> : ISignalFunction2 {
     operator fun invoke(a: A, b: B)
}