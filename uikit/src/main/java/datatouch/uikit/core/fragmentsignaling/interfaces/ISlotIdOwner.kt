package datatouch.uikit.core.fragmentsignaling.interfaces

import datatouch.uikit.core.fragmentsignaling.base.SigSlotId

interface ISlotIdOwner : IDropableSignal {
    val slotId: SigSlotId
}