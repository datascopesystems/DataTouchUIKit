package datatouch.uikit.core.fragmentsignaling.consumer.property

import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentsignaling.consumer.FragmentSignalConsumer
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotCreationContainer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class FragmentSignalConsumerProperty<T : Fragment> : ReadOnlyProperty<T, SlotCreationContainer> {
    private val signalConsumer = FragmentSignalConsumer()

    override fun getValue(thisRef: T, property: KProperty<*>): SlotCreationContainer {
        signalConsumer.configure(thisRef)
        return signalConsumer
    }
}