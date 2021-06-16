package datatouch.uikit.core.fragmentsignaling.interfaces

fun interface ISlotRetValAction<R> {
    operator fun invoke(retVal: R)
}