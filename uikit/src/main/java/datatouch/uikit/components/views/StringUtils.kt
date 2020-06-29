package datatouch.uikit.components.views

object StringUtils {
    fun repeat(s: String?, n: Int): String? {
        if (s == null) {
            return null
        }
        val sb = StringBuilder(s.length * n)
        for (i in 0 until n) {
            sb.append(s)
        }
        return sb.toString()
    }
}
