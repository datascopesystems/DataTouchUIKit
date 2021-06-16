package datatouch.uikit.core.fragmentsignaling.interfaces

import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty

interface ISigCallProperty<V> : ReadWriteProperty<Fragment, V>
