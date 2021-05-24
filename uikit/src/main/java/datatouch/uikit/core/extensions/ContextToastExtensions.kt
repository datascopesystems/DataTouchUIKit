package datatouch.uikit.core.extensions

import android.content.Context
import datatouch.uikit.components.toast.ToastNotification

object ContextToastExtensions {

    fun Context?.err(resId: Int) = ToastNotification.showError(this, resId)
    fun Context?.err(message: String?) = ToastNotification.showError(this, message)
    fun Context?.success(resId: Int) = ToastNotification.showSuccess(this, resId)
    fun Context?.success(message: String?) = ToastNotification.showSuccess(this, message)
    fun Context?.info(resId: Int) = ToastNotification.showInfo(this, resId)
    fun Context?.info(message: String?) = ToastNotification.showInfo(this, message)
    fun Context?.warn(resId: Int) = ToastNotification.showWarning(this, resId)
    fun Context?.warn(message: String?) = ToastNotification.showWarning(this, message)

}