package datatouch.uikit.components.appbackground

enum class AppBackgroundEnum(private val value: Int) {
    RiverThames(0), ModernLondon(1), NewYork(2);

    fun toInt() = value

    companion object {
        fun fromInt(i: Int): AppBackgroundEnum {
            return when (i) {
                0 -> RiverThames
                1 -> ModernLondon
                2 -> NewYork
                else -> RiverThames
            }
        }
    }
}