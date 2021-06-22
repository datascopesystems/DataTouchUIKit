package datatouch.uikit.core.activityargs.extension

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

fun <T : Serializable?> Intent.putArg(property: KMutableProperty<T>, value: T): Intent {
    this.putExtra(makeIntentKeyName(property.name), value)
    return this
}

fun <T : AppCompatActivity> Intent.withClass(context: Context?, klass: KClass<T>): Intent {
    if (context != null) {
        setClass(context, klass.java)
    }
    return this
}

fun Intent.startActivity(context: Context?) {
    context?.startActivity(this)
}


