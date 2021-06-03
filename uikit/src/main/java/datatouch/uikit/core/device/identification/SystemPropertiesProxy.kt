package datatouch.uikit.core.device.identification
import android.annotation.SuppressLint
import android.content.Context
import kotlin.jvm.Throws

object SystemPropertiesProxy {

    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun get(context: Context?, key: String, def: String): String {
        var ret: String

        try {
            val cl = context?.classLoader
            val systemProps = cl?.loadClass("android.os.SystemProperties")

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = String::class.java

            val get = systemProps?.getMethod("get", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def

            ret = get?.invoke(systemProps, params) as? String? ?: ""
        } catch (e: Throwable) {
            ret = def
        }

        return ret
    }

}