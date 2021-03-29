package datatouch.uikit.components.appbackground

import androidx.annotation.DrawableRes
import datatouch.uikit.R
import java.io.Serializable
import java.util.*

class AppBackground private constructor(
        val appBackgroundEnum: AppBackgroundEnum,
        val name: String,
        @DrawableRes val normalBackground: Int,
        @DrawableRes val blurredBackground: Int) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AppBackground?
        return appBackgroundEnum == that?.appBackgroundEnum
    }

    override fun hashCode(): Int {
        return appBackgroundEnum.toInt()
    }

    class List : ArrayList<AppBackground>() {

        fun find(appBackgroundEnum: AppBackgroundEnum): AppBackground {
            return first { it.appBackgroundEnum == appBackgroundEnum }
        }

        companion object {

            @JvmStatic
            fun createFromAll(): List {
                val list = List()

                list.add(AppBackground(AppBackgroundEnum.RiverThames, "City Of London",
                        R.drawable.app_background_city_of_london,
                        R.drawable.app_background_blur_city_of_london))

                list.add(AppBackground(AppBackgroundEnum.RiverThames, "River Thames",
                        R.drawable.app_background_london_thames,
                        R.drawable.app_background_blur_london_thames))

                list.add(AppBackground(AppBackgroundEnum.ModernLondon, "Modern London",
                        R.drawable.app_background_modern_london,
                        R.drawable.app_background_blur_modern_london))

                list.add(AppBackground(AppBackgroundEnum.NewYork, "New York",
                        R.drawable.app_background_new_york,
                        R.drawable.app_background_blur_new_york))

                return list
            }
        }
    }
}