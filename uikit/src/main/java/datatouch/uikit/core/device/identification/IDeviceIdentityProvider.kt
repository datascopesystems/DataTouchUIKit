package datatouch.uikit.core.device.identification

interface IDeviceIdentityProvider {

    suspend fun getDeviceIdAsync(): String

    fun getDeviceType(): DeviceType

}