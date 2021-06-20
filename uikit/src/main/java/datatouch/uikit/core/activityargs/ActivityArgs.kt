package datatouch.uikit.core.activityargs

import datatouch.uikit.core.activityargs.interfaces.IActivityArgProperty
import datatouch.uikit.core.activityargs.property.ActivityArgProperty
import datatouch.uikit.core.activityargs.property.ActivityArgPropertyNullable
import java.io.Serializable

object ActivityArgs {

    fun <T : Serializable> of(arg: T): IActivityArgProperty<T> = ActivityArgProperty(arg)

    fun <T : Serializable> ofNullable(arg: T?): IActivityArgProperty<T?> = ActivityArgPropertyNullable(arg)
}