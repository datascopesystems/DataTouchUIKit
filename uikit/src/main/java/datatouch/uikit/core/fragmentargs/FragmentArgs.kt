package datatouch.utils.fragmentargs

import datatouch.uikit.core.fragmentargs.interfaces.IFragmentArgProperty
import datatouch.uikit.core.fragmentargs.property.ArgProperty
import datatouch.uikit.core.fragmentargs.property.ArgPropertyNullable
import java.io.Serializable

object FragmentArgs {

    fun <T : Serializable> of(arg: T): IFragmentArgProperty<T> = ArgProperty(arg)

    fun <T : Serializable> ofNullable(arg: T?): IFragmentArgProperty<T?> = ArgPropertyNullable(arg)
}