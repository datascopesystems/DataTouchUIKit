package datatouch.uikit.core.device.identification

interface IDeviceIdentityProvider {

    fun getDeviceIdAsync(): String

    fun getDeviceType(): DeviceType

}