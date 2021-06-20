package datatouch.uikit.core.activityargs.interfaces

import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.ReadWriteProperty


interface IActivityArgProperty<V> : ReadWriteProperty<AppCompatActivity, V>
