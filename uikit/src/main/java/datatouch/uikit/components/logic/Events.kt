package datatouch.uikit.components.logic

import datatouch.uikit.core.extensions.BooleanExtensions.ifNo
import datatouch.uikit.core.extensions.BooleanExtensions.ifYes
import de.greenrobot.event.EventBus.getDefault

object Events {

    @JvmStatic
    fun post(event: Event?) = getDefault().post(event)

    @JvmStatic
    fun post(eventId: Int) = getDefault().post(Event(eventId))

    @JvmStatic
    fun register(src: Any?) = getDefault().isRegistered(src).ifNo {
        getDefault().register(src)
    }

    @JvmStatic
    fun unregister(src: Any?) = getDefault().isRegistered(src).ifYes {
        getDefault().unregister(src)
    }

}