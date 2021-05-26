package datatouch.uikit.components

import datatouch.uikit.BuildConfig

object Environment {

    private var isDebugEnv: Boolean? = null

    fun setIsDebug(isDebug: Boolean) {
        isDebugEnv = isDebug
    }

    val isDebug: Boolean get() = isDebugEnv?.apply {} ?: BuildConfig.DEBUG
}
