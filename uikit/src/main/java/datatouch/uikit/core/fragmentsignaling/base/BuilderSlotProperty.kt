package datatouch.uikit.core.fragmentsignaling.base

import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotContainer

abstract class BuilderSlotProperty(slotContainer: SlotContainer) {
    private var container: SlotContainer? = slotContainer

    protected fun getSlotContainerOnce(): SlotContainer {
        val c = container!!
        container = null
        return c
    }
}