package datatouch.uikit.components.toast

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty.*

object ToastNotification {

    @JvmStatic
    fun showWarning(ctx: Context?, message: String?) = ctx?.apply {
        warning(ctx, message ?: "", Toast.LENGTH_LONG, true).show()
    }

    @JvmStatic
    fun showWarning(activity: Context?, message: Int) = activity?.apply {
        warning(this, resources.getString(message), Toast.LENGTH_LONG, true).show()
    }

    @JvmStatic
    fun showError(ctx: Context?, message: String?) = ctx?.apply {
        error(this, message ?: "").show()
    }

    @JvmStatic
    fun showError(ctx: Context?, message: Int) = ctx?.apply {
        error(this, resources.getString(message)).show()
    }

    @JvmStatic
    fun showSuccess(ctx: Context?, message: String?) = ctx?.apply {
        success(this, message ?: "").show()
    }

    @JvmStatic
    fun showSuccess(ctx: Context?, message: Int) = ctx?.apply {
        success(this, resources.getString(message)).show()
    }

    @JvmStatic
    fun showInfo(ctx: Context?, message: String?) = ctx?.apply {
        info(this, message ?: "").show()
    }

    @JvmStatic
    fun showInfo(ctx: Context?, message: Int) = ctx?.apply {
        info(this, resources.getString(message)).show()
    }


}