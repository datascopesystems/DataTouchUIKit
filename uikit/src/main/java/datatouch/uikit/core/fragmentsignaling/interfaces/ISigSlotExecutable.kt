package datatouch.uikit.core.fragmentsignaling.interfaces

import datatouch.uikit.core.fragmentsignaling.base.SigSlotId
import datatouch.uikit.core.fragmentsignaling.base.SlotExecResult

internal interface ISigSlotExecutable: ISlotIdOwner {
    fun execute(args: Array<Any?>?): SlotExecResult
    fun isSlotNumberEquals(id: SigSlotId): Boolean
}