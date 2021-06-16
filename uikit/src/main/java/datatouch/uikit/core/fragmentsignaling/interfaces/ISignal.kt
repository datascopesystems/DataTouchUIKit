package datatouch.uikit.core.fragmentsignaling.interfaces

interface ISignal : ISlotIdOwner {
    fun getSlotParameters(): Array<Any?>?
    fun execRetValAction(returnValue: Any?)
    fun isNotBelongsToConsumer(consumerName: String): Boolean
}