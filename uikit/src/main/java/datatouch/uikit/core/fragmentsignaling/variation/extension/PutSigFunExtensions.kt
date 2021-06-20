package datatouch.uikit.core.fragmentsignaling.variation.extension

import android.os.Bundle
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun0
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun1
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun2
import kotlin.reflect.KProperty

// Extension for signals which has return value
fun <R, T : SigFun0<R>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}

fun <A, R, T : SigFun1<A, R>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}

fun <A, B, R, T : SigFun2<A, B, R>> Bundle.putSignal(property: KProperty<T>, value: T): Bundle {
    putSerializable(property.name, value.slotId)
    return this
}