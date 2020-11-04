package datatouch.uikit.core.utils

object Conditions {

    @JvmStatic
    fun selectNotLessThan(min: Int, value: Int): Int {
        return if (value < min) min else value
    }

    @JvmStatic
    fun isNotNullOrEmpty(list: Collection<*>?): Boolean {
        return list != null && !list.isEmpty()
    }

    @JvmStatic
    fun isNullOrZero(integer: Int?): Boolean {
        return isNull(integer) || integer == 0
    }

    @JvmStatic
    fun isNotNullOrZero(integer: Int?): Boolean {
        return !isNullOrZero(integer)
    }

    @JvmStatic
    fun isNullOrEmpty(string: String?): Boolean {
        return string == null || string.isEmpty() || string.trim { it <= ' ' }
            .isEmpty()
    }

    @JvmStatic
    fun isNullOrEmpty(jArray: Set<*>?): Boolean {
        return jArray == null || jArray.isEmpty()
    }

    @JvmStatic
    fun isNullOrEmpty(jArray: Collection<*>?): Boolean {
        return jArray == null|| jArray.isEmpty()
    }

    @JvmStatic
    fun isNullOrEmpty(charSequence: CharSequence?): Boolean {
        return charSequence == null || charSequence.isEmpty()
    }

    @JvmStatic
    fun isNotNullOrEmpty(charSequence: CharSequence?): Boolean {
        return charSequence != null && charSequence.isNotEmpty()
    }

    @JvmStatic
    fun isNotNullOrEmptyString(charSequence: String?): Boolean {
        return charSequence != null && charSequence.trim { it <= ' ' }.isNotEmpty()
    }

    @JvmStatic
    fun isNullOrEmptyString(string: String?): Boolean {
        return !isNotNullOrEmptyString(string)
    }

    @JvmStatic
    fun isNullOrEmpty(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }

    @JvmStatic
    fun getIntValueIfValid(intStr: String?): Int {
        return if (intStr != null && intStr.isNotEmpty()) {
            Integer.valueOf(intStr)
        } else 0
    }

    @JvmStatic
    fun isNullOrEmpty(stringArray: Array<String?>?): Boolean {
        return stringArray == null || stringArray.isEmpty()
    }

    @JvmStatic
    fun isNull(obj: Any?): Boolean {
        return null == obj
    }

    @JvmStatic
    fun isNotNull(obj: Any?): Boolean {
        return null != obj
    }

    @JvmStatic
    fun isNullArray(vararg objects: Any?): Boolean {
        for (`object` in objects) {
            if (`object` == null) {
                return true
            }
        }
        return false
    }
}