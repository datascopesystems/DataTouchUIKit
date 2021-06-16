package datatouch.uikit.core.fragmentargs.extension

import android.os.Bundle
import java.io.Serializable
import kotlin.reflect.KMutableProperty

fun <T : Serializable?> Bundle.putArg(property: KMutableProperty<T>, value: T): Bundle {
    putSerializable(property.name, value)
    return this
}