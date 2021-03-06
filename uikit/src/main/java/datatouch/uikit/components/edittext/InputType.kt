package datatouch.uikit.components.edittext

enum class InputType(private val value: Int) {
    Text(0), Number(1), TextMultiline(2);

    fun toInt() = value

    companion object {
        fun fromInt(i: Int): InputType {
            return when (i) {
                0 -> Text
                1 -> Number
                2 -> TextMultiline
                else -> Text
            }
        }
    }
}