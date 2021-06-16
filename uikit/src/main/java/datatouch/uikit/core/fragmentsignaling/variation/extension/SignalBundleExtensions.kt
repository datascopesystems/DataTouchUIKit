package datatouch.uikit.core.fragmentsignaling.variation.extension

import android.os.Bundle
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall1
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import kotlin.reflect.KProperty

fun <R, T : SigCall0<R>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}

fun <A, R, T : SigCall1<A, R>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}

fun <A, B, R, T : SigCall2<A, B, R>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}