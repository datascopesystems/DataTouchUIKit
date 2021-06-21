package datatouch.uikit.core.fragmentsignaling

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotCreationContainer
import datatouch.uikit.core.fragmentsignaling.consumer.property.ActivitySignalConsumerProperty
import datatouch.uikit.core.fragmentsignaling.consumer.property.FragmentSignalConsumerProperty
import datatouch.uikit.core.fragmentsignaling.variation.builders.BuilderSigFun0
import datatouch.uikit.core.fragmentsignaling.variation.builders.BuilderSigFun1
import datatouch.uikit.core.fragmentsignaling.variation.builders.BuilderSigFun2
import kotlin.properties.ReadOnlyProperty

object SigFactory {

    /**
     *  Create sigFun builder for signal without params
     */
    fun sigFun(): BuilderSigFun0 {
        return BuilderSigFun0()
    }

    /**
     *  Create sigFun builder for signal with 1 param
     *
     *  A - param type
     *  @param a - must be ignored; it is used for overload only
     */
    fun <A> sigFun(a: A? = null): BuilderSigFun1<A> {
        return BuilderSigFun1()
    }

    /**
     *  Create sigFun builder for signal with 2 params
     *
     *  A, B - param types
     *  @param a - must be ignored; it is used for overload only
     *  @param b - must be ignored; it is used for overload only
     */
    fun <A, B> sigFun(a: A? = null, b: B? = null): BuilderSigFun2<A, B> {
        return BuilderSigFun2()
    }


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