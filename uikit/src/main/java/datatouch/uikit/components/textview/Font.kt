package datatouch.uikit.components.textview

enum class Font(val value: Int) {

    Regular(0), Bold(1), Italic(2);


    companion object {

        @JvmStatic
        fun fromValue(value: Int): Font {
            return when (value) {
                0 -> Regular
                1 -> Bold
                2 -> Italic
                else -> Regular
            }
        }

    }

}