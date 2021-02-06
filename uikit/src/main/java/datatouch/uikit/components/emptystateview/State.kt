package datatouch.uikit.components.emptystateview

enum class State(val value: Int) {
    Loading(0), Empty(1), Container(2);

    companion object {
        @JvmStatic
        fun fromValue(value: Int): State {
            return when (value) {
                0 -> Loading
                1 -> Empty
                2 -> Container
                else -> Loading
            }
        }
    }

}