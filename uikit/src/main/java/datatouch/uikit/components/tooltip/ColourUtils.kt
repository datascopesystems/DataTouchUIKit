package datatouch.uikit.components.tooltip

import android.graphics.Color

object ColourUtils {
    const val DEFAULT_COLOUR = Color.BLACK

    @JvmStatic
    fun convertStringColorArrayToIntColors(arrayOfColorStrings: Array<String?>) = IntArray(arrayOfColorStrings.size).also {
        arrayOfColorStrings.indices.forEach { index -> it[index] = Color.parseColor(arrayOfColorStrings[index]) }
    }

    @JvmStatic
    fun getComplimentColor(color: Int): Int {
        fun mask(src: Int) = src.inv() and 0xff
        return Color.argb(
            Color.alpha(color),
            mask(Color.red(color)),
            mask(Color.green(color)),
            mask(Color.blue(color))
        )
    }

    @JvmStatic
    fun makeColorTransparent(color: Int, alpha: Int) =
        Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

    @JvmStatic
    @JvmOverloads
    fun parseColor(colour: String?, defaultColour: Int = DEFAULT_COLOUR) = try {
        Color.parseColor(colour)
    } catch (ex: Exception) {
        defaultColour
    }

    @JvmStatic
    fun getHexStringColor(intColor: Int) = String.format("#%06X", 0xFFFFFF and intColor)
}