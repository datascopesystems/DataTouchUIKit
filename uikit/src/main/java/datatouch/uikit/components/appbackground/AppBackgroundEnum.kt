package datatouch.uikit.components.appbackground

enum class AppBackgroundEnum(private val value: Int) {
    CityOfLondon(0), RiverThames(1), ModernLondon(2), NewYork(3);

    fun toInt() = value

    companion object {
        fun fromInt(i: Int): AppBackgroundEnum {
            return when (i) {
                0 -> CityOfLondon
                1 -> RiverThames
                2 -> ModernLondon
                3 -> NewYork
                else -> CityOfLondon
            }
        }
    }
}