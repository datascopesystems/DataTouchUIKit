package datatouch.uikit.core.device.identification

import android.content.Context
import datatouch.uikit.core.extensions.StringExtensions.isNotEmptyOrZeroes

const val DefaultMac = "02:00:00:00:00:00"

object HardwareIdUtils {

    @JvmStatic
    fun getHardwareId(context: Context?): String? {
        var hardwareId: String? = null

        for (id in HardwareIdType.values()) {

            kotlin.runCatching { hardwareId = id.getId(context) }

            if (hardwareId.equals(DefaultMac)) {
                continue
            }
            if (hardwareId.isNotEmptyOrZeroes()) return hardwareId
        }

        return hardwareId
    }

}