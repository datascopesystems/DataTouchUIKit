package datatouch.uikit.components.statefulrecyclerview

enum class LayoutType(val value: Int) {

    LinearVertical(0), LinearHorizontal(1), Grid(2);

    companion object {
        @JvmStatic
        fun fromValue(value: Int): LayoutType {
            return when (value) {
                0 -> LinearVertical
                1 -> LinearHorizontal
                2 -> Grid
                else -> LinearVertical
            }
        }
    }

}