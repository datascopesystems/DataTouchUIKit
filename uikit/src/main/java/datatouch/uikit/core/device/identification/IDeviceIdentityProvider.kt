package datatouch.uikit.core.device.identification

interface IDeviceIdentityProvider {

    fun getDeviceId(): String

    fun getDeviceType(): DeviceType

}