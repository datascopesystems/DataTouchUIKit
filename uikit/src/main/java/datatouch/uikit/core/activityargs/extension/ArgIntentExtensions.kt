package datatouch.uikit.core.activityargs.extension

import android.content.Intent
import java.io.Serializable
import kotlin.reflect.KMutableProperty

fun <T : Serializable?> Intent.putArg(property: KMutableProperty<T>, value: T): Intent {
    this.putExtra(makeIntentKeyName(property.name), value)
    return this
}