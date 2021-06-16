package datatouch.uikit.core.fragmentsignaling.base

import java.io.Serializable

class SigSlotId(private val consumerName: String,
                val slotNumber: Int): Serializable {

    fun isBelongsToConsumer(consumerName: String): Boolean {
        return when (isNotEmpty()) {
            true -> this.consumerName == consumerName
            else -> false
        }
    }

    fun isNotBelongsToConsumer(consumerName: String) = !isBelongsToConsumer(consumerName)

    fun isEmpty(): Boolean = consumerName.isEmpty() || slotNumber < 0
    fun isNotEmpty(): Boolean = !isEmpty()

    fun isSlotNumberEquals(id: SigSlotId): Boolean {
        return slotNumber == id.slotNumber
    }

    override fun toString(): String {
        return "[consumerName=$consumerName  handlerNumber=$slotNumber]"
    }

    companion object {
        fun createEmpty() = SigSlotId("", -1)
    }
}