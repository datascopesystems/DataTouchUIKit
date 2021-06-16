package datatouch.uikit.core.fragmentargs.interfaces

import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty

interface IFragmentArgProperty<V> : ReadWriteProperty<Fragment, V>