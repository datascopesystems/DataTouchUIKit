package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.extensions.GenericExtensions.default
import java.io.Serializable

class SigSlotId(private val consumerName: String,
                val slotNumber: Int): Serializable {

    private var slotName: String? = null

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

    internal fun assignSlotName(name: String?) {
        slotName = name
    }

    internal fun hasSlotName(): Boolean = slotName != null

    internal fun getSlotName(): String {
        return slotName.default("")
    }

    override fun toString(): String {
        return "[consumerName=$consumerName slotName=$slotName slotNumber=$slotNumber]"
    }

    companion object {
        fun createEmpty() = SigSlotId("", -1)
    }
}