package datatouch.uikit.components.buttons

enum class ButtonType {

    Positive, Negative, Neutral;

    companion object {
        fun fromInt(i: Int): ButtonType {
            return when (i) {
                0 -> Positive
                1 -> Negative
                2 -> Neutral
                else -> Positive
            }
        }
    }

}