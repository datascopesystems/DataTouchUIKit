package datatouch.uikit.components.floatinggroupexpandablelistview

import android.util.Log

internal object ReflectionUtils {

    private val TAG = ReflectionUtils::class.java.name

    @JvmStatic
    fun getFieldValue(fieldClass: Class<*>, fieldName: String?, instance: Any?): Any? {
        try {
            val field = fieldClass.getDeclaredField(fieldName!!)
            field.isAccessible = true
            return field[instance]
        } catch (e: Exception) {
        }
        return null
    }

    @JvmStatic
    fun setFieldValue(fieldClass: Class<*>, fieldName: String?, instance: Any?, value: Any?) {
        try {
            val field = fieldClass.getDeclaredField(fieldName!!)
            field.isAccessible = true
            field[instance] = value
        } catch (e: Exception) {
            Log.w(TAG, Log.getStackTraceString(e))
        }
    }

    @JvmStatic
    fun invokeMethod(methodClass: Class<*>, methodName: String?, parameters: Array<Class<*>?>, instance: Any?, vararg arguments: Any?) {
        try {
            val method = methodClass.getDeclaredMethod(methodName!!, *parameters)
            method.isAccessible = true
            method.invoke(instance, *arguments)
        } catch (e: Exception) {
            Log.w(TAG, Log.getStackTraceString(e))
        }
    }
}