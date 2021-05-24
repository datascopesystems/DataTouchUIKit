package datatouch.uikit.components.wizard

sealed class StepFragmentResult {
    object Complete : StepFragmentResult()
    object Incomplete : StepFragmentResult()
}