package datatouch.uikit.core.fragmentsignaling

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotCreationContainer
import datatouch.uikit.core.fragmentsignaling.consumer.property.ActivitySignalConsumerProperty
import datatouch.uikit.core.fragmentsignaling.consumer.property.FragmentSignalConsumerProperty
import datatouch.uikit.core.fragmentsignaling.interfaces.ISigCallProperty
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall1
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigCallInvokerProperty0
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigCallInvokerProperty1
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigCallInvokerProperty2
import kotlin.properties.ReadOnlyProperty

object SigFactory {

    // SigCall delegates; Used in child fragment
    fun <R> sigCall(): ISigCallProperty<SigCall0<R>> {
        return SigCallInvokerProperty0()
    }

    fun <A, R> sigCall(a: A? = null): ISigCallProperty<SigCall1<A, R>> {
        return SigCallInvokerProperty1()
    }

    fun <A, B, R> sigCall(a: A? = null, b: B? = null): ISigCallProperty<SigCall2<A, B, R>> {
        return SigCallInvokerProperty2()
    }

    // Signal slot container delegates; Used in Activity or parent Fragment
    fun <T : AppCompatActivity> slotContainerActivity(): ReadOnlyProperty<T, SlotCreationContainer> {
        return ActivitySignalConsumerProperty()
    }

    fun <T : Fragment> slotContainerFragment(): ReadOnlyProperty<T, SlotCreationContainer> {
        return FragmentSignalConsumerProperty()
    }
}