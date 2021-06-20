package datatouch.uikit.core.fragmentsignaling.variation.extension

import android.os.Bundle
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*
import kotlin.reflect.KProperty

// Extension for signals WITHOUT return value
fun <T : SigFunVoid0> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}

fun <A, T : SigFunVoid1<A>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}

fun <A, B, T : SigFunVoid2<A, B>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}