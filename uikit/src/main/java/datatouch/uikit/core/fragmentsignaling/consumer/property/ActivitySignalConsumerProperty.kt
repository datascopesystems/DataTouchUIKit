package datatouch.uikit.core.fragmentsignaling.consumer.property

import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.core.fragmentsignaling.consumer.ActivitySignalConsumer
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotCreationContainer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


internal class ActivitySignalConsumerProperty<T : AppCompatActivity> : ReadOnlyProperty<T, SlotCreationContainer> {
    private val signalConsumer = ActivitySignalConsumer()

    override fun getValue(thisRef: T, property: KProperty<*>): SlotCreationContainer {
        signalConsumer.configure(thisRef)
        return signalConsumer
    }
}