package datatouch.uikit.core.device.identification

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.NetworkInterface
import java.util.*
import kotlin.jvm.Throws

private const val FileAddressMac = "/sys/class/net/wlan0/address"
private const val PropertySerialNumber = "ro.serialno"
private const val BuggyAndroidId = "9774d56d682e549c"
private const val AndroidIdBugMessage = ("The device suffers from "
        + "the Android ID bug - its ID is the emulator ID : "
        + BuggyAndroidId)

@Suppress("unused")
enum class HardwareIdType {

    WifiMac {
        @SuppressLint("HardwareIds")
        override fun getId(context: Context?): String? {
            val wm = context?.applicationContext
                ?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            assertPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
            return recapAddressMac(wm)
        }
    },

    BluetoothMac {
        /**
         * getMacAddress() returns the unique bluetooth hardware address
         * Works only if bluetooth module is present
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        override fun getId(context: Context?): String? {
            val ba = BluetoothAdapter.getDefaultAdapter()
            if (ba == null) {
                return null
            }
            assertPermission(context, Manifest.permission.BLUETOOTH)
            return ba.address
        }
    },
    SerialNumber {
        /**
         * System Property ro.serialno returns the serial number as unique number
         * Works for Android 2.3 and above
         */
        override fun getId(context: Context?): String {
            // no permission needed !
            return SystemPropertiesProxy[context, PropertySerialNumber, DefaultMac]
        }
    },

    Imei {
        /**
         * getDescription() function Returns the unique device ID.
         * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
         */
        @Suppress("DEPRECATION")
        @SuppressLint("MissingPermission", "HardwareIds")
        override fun getId(context: Context?): String? {
            val tm = context
                ?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            assertPermission(context, Manifest.permission.READ_PHONE_STATE)
            return tm.deviceId
        }
    },

    Imsi {
        /**
         * getSubscriberId() function Returns the unique subscriber ID,
         * for example, the IMSI for a GSM phone.
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        override fun getId(context: Context?): String? {
            val tm = context
                ?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            assertPermission(context, Manifest.permission.READ_PHONE_STATE)
            return tm.subscriberId
        }
    },

    AndroidId {
        /**
         * Settings.Secure.ANDROID_ID returns the unique DeviceID
         * Works for Android 2.2 and above
         */
        @SuppressLint("HardwareIds")
        override fun getId(context: Context?): String {
            // no permission needed !
            val androidId = Settings.Secure.getString(
                context?.contentResolver, Settings.Secure.ANDROID_ID
            )

            if (BuggyAndroidId == androidId) {
                throw Exception("Device ID Not Unique")
            }
            return androidId
        }
    },

    SimSerialNumber {
        /**
         * getSimSerialNumber() function Returns the unique sim card ID,
         * for example, the SIM Card ID for a GSM phone.
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        override fun getId(context: Context?): String? {
            val tm = context
                ?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            assertPermission(context, Manifest.permission.READ_PHONE_STATE)
            return tm.simSerialNumber
        }
    },

    PseudoId {
        /**
         * returns a generated pseudo unique ID
         * Works for all devices and Android versions
         */
        override fun getId(context: Context?): String {
            return pseudoDeviceId
        }
    };

    internal fun assertPermission(context: Context?, perm: String) {
        val checkPermission = context?.packageManager?.checkPermission(
            perm, context.packageName
        )
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Permission $perm is required")
        }
    }

    @SuppressLint("HardwareIds")
    internal fun recapAddressMac(wifiMan: WifiManager): String? {
        val wifiInf = wifiMan.connectionInfo

        if (wifiInf.macAddress == DefaultMac) {
            var ret: String?
            try {
                ret = getAddressMacByInterface()
                return if (ret != null) {
                    ret
                } else {
                    ret = getAddressMacByFile(wifiMan)
                    ret
                }
            } catch (e: IOException) {
            } catch (e: Exception) {
            }

        } else {
            return wifiInf.macAddress
        }
        return DefaultMac
    }

    @Suppress("DEPRECATION")
    @Throws(Exception::class)
    private fun getAddressMacByFile(wifiMan: WifiManager): String {
        val wifiState = wifiMan.wifiState

        wifiMan.isWifiEnabled = true
        val fl = File(FileAddressMac)
        val fin = FileInputStream(fl)
        val inputAsString = fin.bufferedReader().use { it.readText() }
        fin.close()
        val enabled = WifiManager.WIFI_STATE_ENABLED == wifiState
        wifiMan.isWifiEnabled = enabled
        return inputAsString
    }

    private fun getAddressMacByInterface(): String? {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (nif.name.equals("wlan0", true)) {
                    val macBytes = nif.hardwareAddress ?: return ""

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }

                    if (res1.isNotEmpty()) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            }

        } catch (e: Exception) {
        }
        return null
    }

    internal abstract fun getId(context: Context?): String?

    /**
     * Returns a generated unique pseudo ID from android.os.Build Constants
     * Works for all devices and Android versions
     *
     * @return a pseudo id - null is never returned
     * see http://www.pocketmagic.net/2011/02/android-unique-device-id/
     */
    //we make this look like a valid IMEI
    //13 digits
    @Suppress("DEPRECATION")
    val pseudoDeviceId: String
        get() = "35" +
                Build.BOARD.length % 10 + Build.BRAND.length % 10 +
                Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 +
                Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
                Build.ID.length % 10 + Build.MANUFACTURER.length % 10 +
                Build.MODEL.length % 10 + Build.PRODUCT.length % 10 +
                Build.TAGS.length % 10 + Build.TYPE.length % 10 +
                Build.USER.length % 10

}