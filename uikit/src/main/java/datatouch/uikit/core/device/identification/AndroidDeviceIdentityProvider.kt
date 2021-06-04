package datatouch.uikit.core.device.identification

import android.content.Context
import datatouch.uikit.R
import datatouch.uikit.core.cache.CacheProvider
import datatouch.uikit.core.utils.Conditions

const val DEVICE_ID_CACHE_KEY = "system.device_id"

class AndroidDeviceIdentityProvider(
    private val context: Context,
    private val cache: CacheProvider
) : IDeviceIdentityProvider {

    override fun getDeviceId(): String {
        val idFromStorage = cache.getString(DEVICE_ID_CACHE_KEY, "")
        if (Conditions.isNotNullOrEmpty(idFromStorage))
            return idFromStorage

        val drmId = DrmIdUtils.getDrmId()
        if (Conditions.isNotNullOrEmpty(drmId.orEmpty())) {
            cache.setString(DEVICE_ID_CACHE_KEY, drmId.orEmpty())
            return drmId.orEmpty()
        }

        val hardwareId = HardwareIdUtils.getHardwareId(context)
        if (Conditions.isNotNullOrEmpty(hardwareId.orEmpty())) {
            cache.setString(DEVICE_ID_CACHE_KEY, hardwareId.orEmpty())
            return hardwareId.orEmpty()
        }

        return context.getString(R.string.device_id_not_available)
    }

    override fun getDeviceType(): DeviceType {
        if (DeviceTypeUtils.isTablet(context)) return DeviceType.Tablet
        if (DeviceTypeUtils.isPhone(context)) return DeviceType.Phone
        if (DeviceTypeUtils.isTV(context)) return DeviceType.TV
        if (DeviceTypeUtils.isWearable(context)) return DeviceType.Wearable
        return DeviceType.Unknown
    }

}