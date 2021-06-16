package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.core.fragmentsignaling.interfaces.IDropableSignal

class SlotExecResult(err: Throwable?, value: Any?) : IDropableSignal {

    var err: Throwable?
        private set

    var value: Any?
        private set

    init {
        this.err = err
        this.value = value
    }

    fun isSuccess(): Boolean = err == null

    override fun drop() {
        value = null
        err = null
    }

    override fun toString(): String {
        return err?.message.default("")
    }

    companion object {
        fun success(value: Any?) = SlotExecResult(null, value)
        fun error(err: Throwable) = SlotExecResult(err, null)
    }
}