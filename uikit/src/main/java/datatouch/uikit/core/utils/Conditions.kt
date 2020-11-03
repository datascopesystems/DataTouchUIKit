package datatouch.uikit.core.utils

object Conditions {
    fun selectNotLessThan(min: Int, value: Int): Int {
        return if (value < min) min else value
    }

    fun isNotNullOrEmpty(list: Collection<*>): Boolean {
        return !isNull(list) && !list.isEmpty()
    }

    fun isNullOrZero(integer: Int): Boolean {
        return isNull(integer) || integer == 0
    }

    fun isNotNullOrZero(integer: Int): Boolean {
        return !isNullOrZero(integer)
    }

    fun isNullOrEmpty(string: String): Boolean {
        return isNull(string) || string.isEmpty() || string.trim { it <= ' ' }
            .isEmpty()
    }

    fun isNullOrEmpty(jArray: Set<*>): Boolean {
        return isNull(jArray) || jArray.isEmpty()
    }

    fun isNullOrEmpty(jArray: Collection<*>): Boolean {
        return isNull(jArray) || jArray.isEmpty()
    }

    fun isNullOrEmpty(charSequence: CharSequence): Boolean {
        return isNull(charSequence) || charSequence.length == 0
    }

    fun isNotNullOrEmpty(charSequence: CharSequence): Boolean {
        return isNotNull(charSequence) && charSequence.length != 0
    }

    fun isNotNullOrEmptyString(charSequence: String): Boolean {
        return isNotNull(charSequence) && charSequence.trim { it <= ' ' }.length != 0
    }

    fun isNullOrEmptyString(string: String): Boolean {
        return !isNotNullOrEmptyString(string)
    }

    fun isNullOrEmpty(map: Map<*, *>): Boolean {
        return isNull(map) || map.isEmpty()
    }

    fun getIntValueIfValid(intStr: String?): Int {
        return if (intStr != null && !intStr.isEmpty()) {
            Integer.valueOf(intStr)
        } else 0
    }

    fun isNullOrEmpty(stringArray: Array<String?>): Boolean {
        return isNull(stringArray) || stringArray.size == 0
    }

    fun isNull(obj: Any?): Boolean {
        return null == obj
    }

    @JvmStatic
    fun isNotNull(obj: Any?): Boolean {
        return null != obj
    }

    fun isNullArray(vararg objects: Any?): Boolean {
        for (`object` in objects) {
            if (`object` == null) {
                return true
            }
        }
        return false
    }
}