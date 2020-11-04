package datatouch.uikit.core.utils

object Conditions {
    fun selectNotLessThan(min: Int, value: Int): Int {
        return if (value < min) min else value
    }

    fun isNotNullOrEmpty(list: Collection<*>?): Boolean {
        return list != null && !list.isEmpty()
    }

    fun isNullOrZero(integer: Int?): Boolean {
        return isNull(integer) || integer == 0
    }

    fun isNotNullOrZero(integer: Int?): Boolean {
        return !isNullOrZero(integer)
    }

    fun isNullOrEmpty(string: String?): Boolean {
        return string == null || string.isEmpty() || string.trim { it <= ' ' }
            .isEmpty()
    }

    fun isNullOrEmpty(jArray: Set<*>?): Boolean {
        return jArray == null || jArray.isEmpty()
    }

    fun isNullOrEmpty(jArray: Collection<*>?): Boolean {
        return jArray == null|| jArray.isEmpty()
    }

    fun isNullOrEmpty(charSequence: CharSequence): Boolean {
        return isNull(charSequence) || charSequence.isEmpty()
    }

    fun isNotNullOrEmpty(charSequence: CharSequence?): Boolean {
        return charSequence != null && charSequence.isNotEmpty()
    }

    fun isNotNullOrEmptyString(charSequence: String?): Boolean {
        return charSequence != null && charSequence.trim { it <= ' ' }.isNotEmpty()
    }

    fun isNullOrEmptyString(string: String?): Boolean {
        return !isNotNullOrEmptyString(string)
    }

    fun isNullOrEmpty(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }

    fun getIntValueIfValid(intStr: String?): Int {
        return if (intStr != null && intStr.isNotEmpty()) {
            Integer.valueOf(intStr)
        } else 0
    }

    fun isNullOrEmpty(stringArray: Array<String?>?): Boolean {
        return stringArray == null || stringArray.isEmpty()
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