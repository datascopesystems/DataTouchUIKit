package datatouch.uikit.core.fragmentsignaling

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotCreationContainer
import datatouch.uikit.core.fragmentsignaling.consumer.property.ActivitySignalConsumerProperty
import datatouch.uikit.core.fragmentsignaling.consumer.property.FragmentSignalConsumerProperty
import datatouch.uikit.core.fragmentsignaling.interfaces.ISigFunProperty
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty0
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty1
import datatouch.uikit.core.fragmentsignaling.variation.invokers.SigFunInvokerProperty2
import datatouch.uikit.core.fragmentsignaling.variation.retval.RetvalSigFunPropertyBuilder
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.*
import kotlin.properties.ReadOnlyProperty

object SigFactory {

    /**
     *  Delegate for SigFunVoid0
     *  signal without params and no return value
     *  @return property delegate for SigFunVoid0 type
     */
    fun sigFun(): ISigFunProperty<SigFunVoid0> {
        return SigFunInvokerProperty0<Unit, SigFunVoid0>()
    }

    /**
     *  Delegate for SigFunVoid1
     *  signal with 1 param and no return value
     *
     *  A - param type
     *  @param a must be ignored; it is used for overload only
     *  @return property delegate for SigFunVoid1 type
     */
    fun <A> sigFun(a: A? = null): ISigFunProperty<SigFunVoid1<A>> {
        return SigFunInvokerProperty1<A, Unit, SigFunVoid1<A>>()
    }

    /**
     *  Delegate for SigFunVoid2
     *  signal 2 params param and no return value
     *
     *  A, B - param types
     *  @param a must be ignored; it is used for overload only
     *  @param b must be ignored; it is used for overload only
     *  @return property delegate for SigFunVoid2 type
     */
    fun <A, B> sigFun(a: A? = null, b: B? = null): ISigFunProperty<SigFunVoid2<A, B>> {
        return SigFunInvokerProperty2<A, B, Unit, SigFunVoid2<A, B>>()
    }

    /**
     *  Specify return value for signals
     *  @return property builder which can create delegates for signals with return value
     */
    fun <R> retVal() = RetvalSigFunPropertyBuilder<R>()


    /**
     * Create slot container for activity, in must be called
     * in activity class
     * @return readonly property delegate for SlotCreationContainer type
     */
    fun <T : AppCompatActivity> slotContainerActivity(): ReadOnlyProperty<T, SlotCreationContainer> {
        return ActivitySignalConsumerProperty()
    }

    /**
     * Create slot container for fragment, in must be called
     * in fragment class
     * @return readonly property delegate for SlotCreationContainer type
     */
    fun <T : Fragment> slotContainerFragment(): ReadOnlyProperty<T, SlotCreationContainer> {
        return FragmentSignalConsumerProperty()
    }
}