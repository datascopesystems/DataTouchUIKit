package datatouch.uikit.core.fragmentsignaling.interfaces

import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty

interface ISigFunProperty<V> : ReadWriteProperty<Fragment, V>
