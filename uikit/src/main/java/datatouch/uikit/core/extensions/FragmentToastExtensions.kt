package datatouch.uikit.core.extensions

import androidx.fragment.app.Fragment
import datatouch.uikit.core.extensions.ContextToastExtensions.err
import datatouch.uikit.core.extensions.ContextToastExtensions.info
import datatouch.uikit.core.extensions.ContextToastExtensions.success
import datatouch.uikit.core.extensions.ContextToastExtensions.warn

object FragmentToastExtensions {

    fun Fragment?.err(resId: Int) = this?.context.err(resId)

    fun Fragment?.err(resId: Int, concatMessage: String?) =
        this?.context.err(this?.context?.getString(resId) + concatMessage)

    fun Fragment?.err(message: String?) = this?.context.err(message)

    fun Fragment?.success(resId: Int) = this?.context.success(resId)

    fun Fragment?.success(message: String?) = this?.context.success(message)

    fun Fragment?.info(resId: Int) = this?.context?.info(resId)

    fun Fragment?.info(message: String?) = this?.context?.info(message)

    fun Fragment?.warn(resId: Int) = this?.context?.warn(resId)

    fun Fragment?.warn(message: String?) = this?.context?.warn(message)

}