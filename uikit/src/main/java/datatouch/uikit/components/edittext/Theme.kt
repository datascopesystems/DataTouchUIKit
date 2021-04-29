package datatouch.uikit.components.edittext

enum class Theme(private val value: Int) {
    Dark(0), Light(1);

    fun toInt() = value

    companion object {
        fun fromInt(i: Int): Theme {
            return when (i) {
                0 -> Dark
                1 -> Light
                else -> Dark
            }
        }
    }
}