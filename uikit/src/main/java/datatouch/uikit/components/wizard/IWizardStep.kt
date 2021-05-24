package datatouch.uikit.components.wizard

import datatouch.uikit.core.extensions.GenericExtensions.default
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface IWizardStep<R> {

    val isComplete: Boolean
    val result: Flow<R>
    val fragment: SuperWizardFragment<R>

    suspend fun stepResultAsync() = result.firstOrNull()
    suspend fun stepResultOrDefault(defaultStepResult: R) = stepResultAsync().default(defaultStepResult)
}