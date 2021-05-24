package datatouch.uikit.components.logger

import datatouch.uikit.components.Environment
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class Logger {

    private val isLoggingDisabled = !Environment.isDebug

    fun e(ex: Exception) {
        if (isLoggingDisabled) return
        Logger.getGlobal().log(Level.SEVERE, ex.message, ex)
    }

    fun e(ex: Throwable) {
        if (isLoggingDisabled) return
        Logger.getGlobal().log(Level.SEVERE, ex.message, ex)
    }

    fun i(message: String?) {
        if (isLoggingDisabled) return
        Logger.getGlobal().log(Level.INFO, message)
    }

    fun e(message: String?) {
        if (isLoggingDisabled) return
        Logger.getGlobal().log(Level.SEVERE, message)
    }

    fun e(tag: String, message: String) {
        if (isLoggingDisabled) return
        Logger.getGlobal().log(Level.SEVERE, "$tag --> $message")
    }

    fun i(message: Int) {
        if (isLoggingDisabled) return
        Logger.getGlobal().log(Level.INFO, message.toString())
    }

    fun printStackTraceLog() =
        Logger.getGlobal().log(Level.SEVERE, Arrays.toString(Throwable().stackTrace))

    fun printStackTrace() =
        Logger.getGlobal().log(Level.SEVERE, Arrays.toString(Throwable().stackTrace))

}