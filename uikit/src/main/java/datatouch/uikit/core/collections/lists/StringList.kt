package datatouch.uikit.core.collections.lists

import java.io.Serializable
import java.util.*

class StringList : ArrayList<String?>, Serializable {

    constructor() : super()
    constructor(array: Array<String?>?) : super(array?.toList().orEmpty())

    private fun getIndexOfString(stringToFind: String?): Int {
        for (i in this.indices) {
            if (this[i] == stringToFind) {
                return i
            }
        }
        return -1
    }

    override fun contains(element: String?): Boolean {
        return getIndexOfString(element) >= 0
    }

    override fun equals(other: Any?): Boolean {
        val toCompareWith = other as StringList?
        if (toCompareWith?.size == size) {
            for (i in 0 until size) {
                if (!toCompareWith.contains(get(i))) {
                    return false
                }
            }
        } else {
            return false
        }
        return true
    }

    override fun hashCode() = super.hashCode()
}