package datatouch.uikit.core.device.identification

import android.media.MediaDrm
import java.util.*

object DrmIdUtils {

    fun getDrmId(): String? {
        runCatching {
            val wideVineId = MediaDrm(
                    UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
            )
                    .getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            return wideVineId.joinToString(separator = "-") { String.format("%02X", it) }
            // Close resources with close() or release() depending on platform API
            // Use ARM on Android P platform or higher, where MediaDrm has the close() method
        }
        return null
    }

}